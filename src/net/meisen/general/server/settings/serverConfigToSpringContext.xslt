<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet 
        xmlns:sc="http://dev.meisen.net/server/config"
        xmlns:scd="http://dev.meisen.net/server/config/default"
        xmlns:sce="http://dev.meisen.net/server/config/extension"
        xmlns:util="http://www.springframework.org/schema/util"
        xmlns:uuid="java.util.UUID"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                
  <xsl:output method="xml" indent="yes" />

  <!-- main template used to write the root (server) -->
  <xsl:template match="/sc:server">
    <beans xmlns="http://www.springframework.org/schema/beans" 
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
                               http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-2.0.xsd">
      
      <!-- define the parameter for the serverSettingsTemplate -->
      <xsl:choose>
        <xsl:when test="scd:defaultMarker">

          <!-- we have to create a default bean -->
          <xsl:call-template name="beanServerSettingsTemplate">
            <xsl:with-param name="isDefault">true</xsl:with-param>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>

          <!-- this seems to be the server settings -->
          <xsl:call-template name="beanServerSettingsTemplate">
            <xsl:with-param name="isDefault">false</xsl:with-param>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </beans>
  </xsl:template>

  <!-- template used to write a serverSettings bean -->
  <xsl:template name="beanServerSettingsTemplate">
    <xsl:param name="isDefault">false</xsl:param>
    <xsl:param name="settingsBeanId">
      <xsl:choose>
        <xsl:when test="$isDefault = 'true'">defaultServerSettings</xsl:when>
        <xsl:otherwise>serverSettings</xsl:otherwise>
      </xsl:choose>
    </xsl:param>

    <!-- create the ServerSettings -->
    <bean id="{$settingsBeanId}" class="net.meisen.general.server.settings.DefaultServerSettings">
      <property name="defaultSettings" value="{$isDefault}" />
      <property name="failOnUnresolvableListeners" value="${server.settings.failOnUnresolvableListeners}" />
    </bean>

    <!-- let's create a bean which adds all the defined connector -->
    <bean id="{$settingsBeanId}MethodInvoker" class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
      <property name="targetObject">
        <ref local="{$settingsBeanId}" />
      </property>
      <property name="targetMethod" value="addConnectorSettings" />
      <property name="arguments">
        <list>
          <xsl:for-each select="sc:connector">
            <xsl:call-template name="beanConnectorTemplate" />
          </xsl:for-each>
        </list>
      </property>      
    </bean>
  </xsl:template>
  
  <!-- template used to write a connector bean -->
  <xsl:template name="beanConnectorTemplate">
    <xsl:variable name="id"><xsl:value-of select="@id"/></xsl:variable>
    <xsl:variable name="listener"><xsl:value-of select="@listener"/></xsl:variable>
    <xsl:variable name="port"><xsl:value-of select="@port"/></xsl:variable>
    <xsl:variable name="enable"><xsl:value-of select="@enable"/></xsl:variable>
    <xsl:variable name="uid" select="uuid:randomUUID()"/>
    
    <bean class="net.meisen.general.server.settings.pojos.Connector">
      <xsl:if test="not(@id)">
        <property name="id" value="{$uid}" />
      </xsl:if>
      <xsl:if test="@id != ''">
        <property name="id" value="{$id}" />
      </xsl:if>
      <xsl:if test="@listener != ''">
        <property name="listener" value="{$listener}" />
      </xsl:if>
      <xsl:if test="@enable != ''">
        <property name="enable" value="{$enable}" />
      </xsl:if>
      <xsl:if test="@port != ''">
        <property name="port" value="{$port}" />
      </xsl:if>
      
      <property name="extensions">
        <list>
          <xsl:for-each select="sce:extension/*">
            <xsl:call-template name="beanExtensionTemplate" />
          </xsl:for-each>
        </list>
      </property>
    </bean>
  </xsl:template>
  
  <!-- template used to write an extension -->
  <xsl:template name="beanExtensionTemplate">
    <xsl:variable name="nodeName"><xsl:value-of select="local-name()"/></xsl:variable>
    <xsl:variable name="nodeText"><xsl:value-of select="text()"/></xsl:variable>
  
    <bean class="net.meisen.general.server.settings.pojos.Extension">
      <property name="id" value="{$nodeName}" />
      
      <!-- get the attributes -->
      <property name="properties">
        <map key-type="java.lang.String" value-type="java.lang.Object">
          <xsl:for-each select="./@*">
            <xsl:variable name="attrName"><xsl:value-of select="name()"/></xsl:variable>
            <xsl:variable name="attrValue"><xsl:value-of select="."/></xsl:variable>
          
            <entry key="{$attrName}" value="{$attrValue}" />
          </xsl:for-each>
          
          <!-- check if we have any text and add it -->
          <xsl:if test="normalize-space(text()) != ''">
            <entry key="" value="{$nodeText}" />
          </xsl:if>
        </map>
      </property>
      
      <!-- get other elements -->
      <property name="extensions">
        <list>
          <xsl:for-each select="./*">
            <xsl:call-template name="beanExtensionTemplate" />
          </xsl:for-each>
        </list>
      </property>
    </bean>
  </xsl:template>
</xsl:stylesheet>