package org.ozwillo.dcimporter.service

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.ozwillo.dcimporter.config.DatacoreProperties
import org.ozwillo.dcimporter.model.marchepublic.*
import org.ozwillo.dcimporter.web.MarchePublicHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DatacoreServiceTest(@Autowired val datacoreProperties: DatacoreProperties,
                          @Autowired val restTemplate: TestRestTemplate,
                          @Autowired val datacoreService: DatacoreService) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MarchePublicHandler::class.java)

        private val MP_PROJECT = "marchepublic_0"
        private val ORG_TYPE = "orgfr:Organisation_0"
        private val CONSULTATION_TYPE = "marchepublic:consultation_0"
        private val LOT_TYPE = "marchepublic:lot_0"
        private val PIECE_TYPE = "marchepublic:piece_0"
    }

    private lateinit var wireMockServer: WireMockServer

    private val siret = "123456789"

    private val tokenInfoResponse = """
        {
            "active": "true"
        }
        """

    private val bearer = "eyJpZCI6ImViMTUzZDRmLTJiY2ItNDRjYS1hMjA2LWIyMTA5MTRmY2ZiOS8zb3l3MmFtUnp4UWMybXlubkhYT1N3IiwiaWF0IjoxNTMxNDkwMDQ5LjE5OTAwMDAwMCwiZXhwIjoxNTMxNDkzNjQ5LjE5OTAwMDAwMH0"

    @BeforeAll
    fun beforeAll() {
        wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().port(8089))
        wireMockServer.start()

        // we need a fake bearer to go through the verification chain
        restTemplate.restTemplate.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
            request.headers["Authorization"] = "Bearer eyJpZCI6IjVjNzVjMWY0LTMzMDQtNDBmZS1hNDZmLTdkOTI2YmRjOTAzZC84UURJb1BZazdGT3pSbngzVlB1cDFRIiwiaWF0IjoxNTMxMTQzOTEwLjg1NDAwMDAwMCwiZXhwIjoxNTMxMTQ3NTEwLjg1NDAwMDAwMH0"
            execution.execute(request, body)
        })

        WireMock.configureFor(8089)
        WireMock.stubFor(WireMock.post(WireMock.urlMatching("/a/tokeninfo"))
                .withHeader("Authorization", EqualToPattern("Basic ZGNpbXBvcnRlcjpNa2xMcm94V1ZGKy9QRFNqazlONkcra29VZTV5T0ZhL1JodEhmVzg5YzZF"))
                .willReturn(WireMock.okJson(tokenInfoResponse).withStatus(200)))
    }

    @AfterAll
    fun afterAll() {
        wireMockServer.stop()
    }

    @Test
    fun saveResourceTest() {
        val reference = "ref-consultation-06"
        val consultation = Consultation(reference = reference,
                objet = "mon marche", datePublication = LocalDateTime.now(), dateCloture = LocalDateTime.now(),
                finaliteMarche = FinaliteMarcheType.MARCHE, typeMarche = TypeMarcheType.PUBLIC,
                typePrestation = TypePrestationType.FOURNITURES, departementsPrestation = listOf(6, 83),
                passation = "passation", informatique = true, passe = "motdepasse", emails = listOf("dev@sictiam.fr", "demat@sictiam.fr"),
                enLigne = false, alloti = false, invisible = false, nbLots = 1)
        val dcConsultation = consultation.toDcObject(datacoreProperties.baseUri, siret)

        datacoreService.saveResource(MP_PROJECT, CONSULTATION_TYPE, dcConsultation, bearer)
    }

    /*@Test
    fun saveLotResourceTest(){
        val reference = "ref-consultation-06"
        val lot = Lot(uuid = UUID.randomUUID().toString(), libelle = "Libellé Lot", ordre = 1, numero = 1)
        val dcLot = lot.toDcObject(datacoreProperties.baseUri, siret, reference)

        datacoreService.saveResource(MP_PROJECT, LOT_TYPE, dcLot, bearer)
    }*/
}