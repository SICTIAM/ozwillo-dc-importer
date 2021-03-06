package org.ozwillo.dcimporter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("application")
class ApplicationProperties {
    lateinit var url: String
}