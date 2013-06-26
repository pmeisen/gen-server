<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet xmlns:tdsc="http://timedata.meisen.net/tdserver/config"
        xmlns:tdscd="http://timedata.meisen.net/tdserver/config/default"
        xmlns:util="http://www.springframework.org/schema/util"
        xmlns:uuid="java.util.UUID"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                
  <xsl:output method="xml" indent="yes" />

  <xsl:template match="/tdsc:tdserver">
    <beans xmlns="http://www.springframework.org/schema/beans" 
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
                               http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-2.0.xsd">
      
      <!-- define the parameter for the template -->
      <xsl:choose>
        <xsl:when test="tdscd:defaultMarker">

          <!-- we have to create a default bean -->
          <xsl:call-template name="beanDefinitions">
            <xsl:with-param name="isDefault">true</xsl:with-param>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>

          <!-- this seems to be the server settings -->
          <xsl:call-template name="beanDefinitions">
            <xsl:with-param name="isDefault">false</xsl:with-param>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    
    </beans>
  </xsl:template>

  <!-- template used to write the beans -->
  <xsl:template name="beanDefinitions">
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
    </bean>

    <!-- let's create a bean which adds all the defined connector -->
    <bean id="{$settingsBeanId}MethodInvoker" class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
      <property name="targetObject">
        <ref local="{$settingsBeanId}" />
      </property>
      <property name="targetMethod">
        <value>addConnectorSettings</value>
      </property>
      <property name="arguments">
        <list>
          <xsl:for-each select="tdsc:connector">
            <xsl:variable name="id"><xsl:value-of select="@id"/></xsl:variable>
            <xsl:variable name="listener"><xsl:value-of select="@listener"/></xsl:variable>
            <xsl:variable name="port"><xsl:value-of select="@port"/></xsl:variable>
            <xsl:variable name="enable"><xsl:value-of select="@enable"/></xsl:variable>
      	    <xsl:variable name="uid" select="uuid:randomUUID()"/>
      	    
            <!-- let's add a ConnectorSetting for each defined connector -->
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
            </bean>
          </xsl:for-each>
        </list>
      </property>      
    </bean>
  </xsl:template>
</xsl:stylesheet>