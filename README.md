jabberwocky
===========

This is a rewrite of my previous Vorpal XMPP framework.

Vorpal can be found here https://java.net/projects/vorpal

Example of WEB-INF/xep-0114.xml

<subdomain name="subdomain_name">

	<!-- mandatory -->
	<domain>jabber.org</domain>
	<shared-secret>hush hush</shared-secret>

	<!-- optional -->
	<port>5272</port> 

	<properties>
		<!-- one or more of these -->
		<property>
			<name>property_name</name>
			<value>property_value</value>
		</property>
	</properties>

</subdomain>