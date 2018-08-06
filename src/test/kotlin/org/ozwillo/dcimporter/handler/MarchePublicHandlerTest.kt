package org.ozwillo.dcimporter.handler

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.ozwillo.dcimporter.OzwilloDcImporterApplication
import org.ozwillo.dcimporter.model.marchepublic.Consultation
import org.ozwillo.dcimporter.model.marchepublic.FinaliteMarcheType
import org.ozwillo.dcimporter.model.marchepublic.TypeMarcheType
import org.ozwillo.dcimporter.model.marchepublic.TypePrestationType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.runApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.*
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TestBase(){
    protected val client = WebTestClient.bindToServer()
                                            .baseUrl("http://localhost:8080")
                                            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                                            .defaultHeader("Authorization", "Bearer bearer")
                                            .build()

    @BeforeAll
    fun initApplication(){
        runApplication<OzwilloDcImporterApplication>()
    }
}

class MarchePublicHandlerNewTest: TestBase(){

    val siret = "123456789"

    @Test
    fun createConsultationFromHandlertoMSTest(){
        val reference = "rfc-005"

        val consultation = Consultation(reference = reference,
                objet = "mon marche", datePublication = LocalDateTime.now(), dateCloture = LocalDateTime.of(2018,10,1,11,8,45, 9),
                finaliteMarche = FinaliteMarcheType.MARCHE, typeMarche = TypeMarcheType.PUBLIC,
                typePrestation = TypePrestationType.FOURNITURES, departementsPrestation = listOf(6, 83),
                passation = "AORA", informatique = true, passe = "motdepasse", emails = listOf("dev@sictiam.fr", "demat@sictiam.fr"),
                enLigne = false, alloti = false, invisible = false, nbLots = 1)

        client.post()
                .uri("/api/marche-public/$siret/consultation")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(consultation), Consultation::class.java)
                .exchange()
                .expectStatus().isCreated
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
    }

    @Test
    fun getConsultationTest(){
        val reference = "rfc-003"

        val consultation = Consultation(reference = reference,
                objet = "mon marche", datePublication = LocalDateTime.of(2018,8,1,11,49,56, 12), dateCloture = LocalDateTime.of(2018,10,1,11,8,45, 9),
                finaliteMarche = FinaliteMarcheType.MARCHE, typeMarche = TypeMarcheType.PUBLIC,
                typePrestation = TypePrestationType.FOURNITURES, departementsPrestation = listOf(6, 83),
                passation = "AORA", informatique = true, passe = "motdepasse", emails = listOf("dev@sictiam.fr", "demat@sictiam.fr"),
                enLigne = false, alloti = false, invisible = false, nbLots = 1)

        client.get()
                .uri("/api/marche-public/$siret/consultation/$reference")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.reference").isEqualTo(reference)
    }

    @Test
    fun updateConsultationTest(){
        val reference = "rfc-003"

        val consultation = Consultation(reference = reference,
                objet = "mon marche modifié", datePublication = LocalDateTime.of(2018,8,1,11,49,56, 12), dateCloture = LocalDateTime.of(2018,10,1,11,8,45, 9),
                finaliteMarche = FinaliteMarcheType.MARCHE, typeMarche = TypeMarcheType.AUTRE,
                typePrestation = TypePrestationType.FOURNITURES, departementsPrestation = listOf(6, 83),
                passation = "PICO", informatique = true, passe = "motdepasse", emails = listOf("dev@sictiam.fr", "demat@sictiam.fr"),
                enLigne = false, alloti = false, invisible = false, nbLots = 1)

        client.put()
                .uri("/api/marche-public/${siret}/consultation/${reference}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(consultation), Consultation::class.java)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
    }

    @Test
    fun deleteConsultationTest(){
        val reference = "rfc-003"

        val consultation = Consultation(reference = reference,
                objet = "mon marche modifié", datePublication = LocalDateTime.of(2018,8,1,11,49,56, 12), dateCloture = LocalDateTime.of(2018,10,1,11,8,45, 9),
                finaliteMarche = FinaliteMarcheType.MARCHE, typeMarche = TypeMarcheType.AUTRE,
                typePrestation = TypePrestationType.FOURNITURES, departementsPrestation = listOf(6, 83),
                passation = "PICO", informatique = true, passe = "motdepasse", emails = listOf("dev@sictiam.fr", "demat@sictiam.fr"),
                enLigne = false, alloti = false, invisible = false, nbLots = 1)

        client.delete()
                .uri("/api/marche-public/${siret}/consultation/${reference}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
    }

    @Test
    fun publishConsultationTest(){
        val reference = "rfc-003"

        val consultation = Consultation(reference = reference,
                objet = "mon marche", datePublication = LocalDateTime.now(), dateCloture = LocalDateTime.now().plusMonths(3),
                finaliteMarche = FinaliteMarcheType.MARCHE, typeMarche = TypeMarcheType.PUBLIC,
                typePrestation = TypePrestationType.FOURNITURES, departementsPrestation = listOf(6, 83),
                passation = "passation", informatique = true, passe = "motdepasse", emails = listOf("dev@sictiam.fr", "demat@sictiam.fr"),
                enLigne = false, alloti = false, invisible = false, nbLots = 0)

        // Publish
        client.post()
                .uri("/api/marche-public/${siret}/consultation/${reference}/publish", siret, reference)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(consultation), Consultation::class.java)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)

    }
}

class MarchePublicHandlerTest(@Autowired val restTemplate: TestRestTemplate) {

    private lateinit var wireMockServer: WireMockServer

    private val siret = "123456789"

    private val tokenInfoResponse = """
        {
            "active": "true"
        }
        """

    @BeforeAll
    fun beforeAll() {
        wireMockServer = WireMockServer(wireMockConfig().port(8089))
        wireMockServer.start()

        // we need a fake bearer to go through the verification chain
        restTemplate.restTemplate.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
            request.headers["Authorization"] = "Bearer secrettoken"
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
    fun `Test correct creation of a consultation`() {
        val reference = "ref-consultation-000003"
        val dcResponse = """
            {
                "@id": "http://data.ozwillo.com/dc/type/marchepublic:consultation_0/123456/$reference"
            }
            """
        val dcGetResponse = """
            {
                "@id": "http://data.ozwillo.com/dc/type/marchepublic:consultation_0/$siret",
                "version": 1
            }
            """

        WireMock.stubFor(WireMock.post(WireMock.urlMatching("/dc/type/marchepublic:consultation_0"))
                .willReturn(WireMock.okJson(dcResponse).withStatus(201)))
        WireMock.stubFor(WireMock.get(WireMock.urlMatching("dc/type/marchepublic:consultation_0/FR/$siret"))
                .willReturn(WireMock.okJson(dcGetResponse).withStatus(200)))

        val consultation = Consultation(reference = reference,
                objet = "mon marche", datePublication = LocalDateTime.now(), dateCloture = LocalDateTime.now(),
                finaliteMarche = FinaliteMarcheType.MARCHE, typeMarche = TypeMarcheType.PUBLIC,
                typePrestation = TypePrestationType.FOURNITURES, departementsPrestation = listOf(6, 83),
                passation = "passation", informatique = true, passe = "motdepasse", emails = listOf("dev@sictiam.fr", "demat@sictiam.fr"),
                enLigne = false, alloti = false, invisible = false, nbLots = 1)

        val entity = restTemplate.postForEntity<String>("/api/marche-public/$siret/consultation", consultation)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(entity.headers["Location"]).isEqualTo(listOf("http://localhost:3000/api/marche-public/$siret/consultation/$reference"))

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlEqualTo("/dc/type/marchepublic:consultation_0")))
    }

    @Test
    fun `Test bad request sent to the Datacore`() {
        WireMock.stubFor(WireMock.post(WireMock.urlMatching("/dc/type/marchepublic:consultation_0"))
                .willReturn(WireMock.aResponse().withStatus(400)))

        val consultation = Consultation(reference = "ref-consultation",
                objet = "mon marche", datePublication = LocalDateTime.now(), dateCloture = LocalDateTime.now(),
                finaliteMarche = FinaliteMarcheType.MARCHE, typeMarche = TypeMarcheType.PUBLIC,
                typePrestation = TypePrestationType.FOURNITURES, departementsPrestation = listOf(6, 83),
                passation = "passation", informatique = true, passe = "motdepasse", emails = listOf("dev@sictiam.fr", "demat@sictiam.fr"),
                enLigne = false, alloti = false, invisible = false, nbLots = 1)

        val entity = restTemplate.postForEntity<String>("/api/marche-public/$siret/consultation", consultation)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(entity.headers["Location"]).isNull()

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlEqualTo("/dc/type/marchepublic:consultation_0")))
    }

    @Test
    fun `Test illformed consultation payload`() {
        val consultationJson = """
            {
                "reference": "reference",
                "objet": "mon marché public",
                "dateCloture": "2018-05-31T00:00:00",
                "finaliteMarche": "MARCHE",
                "typePrestation": "TRAVAUX",
                "departementsPrestation": [6,83],
                "passation": "Passation",
                "informatique": "true",
                "emails": ["dev@sictiam.fr", "demat@sictiam.fr"],
                "enLigne": "false",
                "alloti": "true",
                "invisible": "false",
                "nbLots": "1"
            }
            """
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(consultationJson, headers)

        val response = restTemplate.exchange("/api/marche-public/$siret/consultation", HttpMethod.POST, entity, String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).startsWith("JSON decoding error")
    }

    @Test
    fun `Test correct update of a consultation`() {
        val reference = "ref-consultation"
        val dcResponse = """
            {
                "@id": "http://data.ozwillo.com/dc/type/marchepublic:consultation_0/123456/$reference"
            }
            """
        WireMock.stubFor(WireMock.post(WireMock.urlMatching("/dc/type/marchepublic:consultation_0"))
                .willReturn(WireMock.okJson(dcResponse).withStatus(201)))
        WireMock.stubFor(WireMock.put(WireMock.urlMatching("/dc/type/marchepublic:consultation_0/FR/$siret/$reference"))
                .willReturn(WireMock.okJson(dcResponse).withStatus(200)))

        val consultation = Consultation(reference = reference,
                objet = "mon marche", datePublication = LocalDateTime.now(), dateCloture = LocalDateTime.now(),
                finaliteMarche = FinaliteMarcheType.MARCHE, typeMarche = TypeMarcheType.PUBLIC,
                typePrestation = TypePrestationType.FOURNITURES, departementsPrestation = listOf(6, 83),
                passation = "passation", informatique = true, passe = "motdepasse", emails = listOf("dev@sictiam.fr", "demat@sictiam.fr"),
                enLigne = false, alloti = false, invisible = false, nbLots = 1)

        val entity = restTemplate.postForEntity<String>("/api/marche-public/$siret/consultation", consultation)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(entity.headers["Location"]).isEqualTo(listOf("http://localhost:3000/api/marche-public/$siret/consultation/$reference"))

        val updatedConsultation = consultation.copy(objet = "mon nouvel objet", typeMarche = TypeMarcheType.PRIVE)
        val httpEntity = HttpEntity(updatedConsultation)
        val updatedEntity = restTemplate.exchange("/api/marche-public/$siret/consultation/$reference",
                HttpMethod.PUT, httpEntity, String::class.java)
        assertThat(updatedEntity.statusCode).isEqualTo(HttpStatus.OK)

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlEqualTo("/dc/type/marchepublic:consultation_0")))
        WireMock.verify(WireMock.putRequestedFor(WireMock.urlEqualTo("/dc/type/marchepublic:consultation_0")))
    }

    @Test
    fun `Test correct delete of a consultation`() {
        val reference = "ref-consultation"
        val dcCreateResponse = """
            {
                "@id": "http://data.ozwillo.com/dc/type/marchepublic:consultation_0/123456/$reference"
            }
            """
        val dcGetResponse = """
            {
                "@id": "http://data.ozwillo.com/dc/type/marchepublic:consultation_0/123456/$reference",
                "version": 1
            }
            """
        WireMock.stubFor(WireMock.post(WireMock.urlMatching("/dc/type/marchepublic:consultation_0"))
                .willReturn(WireMock.okJson(dcCreateResponse).withStatus(201)))
        WireMock.stubFor(WireMock.get(WireMock.urlMatching("/dc/type/marchepublic:consultation_0/FR/$siret/$reference"))
                .willReturn(WireMock.okJson(dcGetResponse).withStatus(200)))
        WireMock.stubFor(WireMock.delete(WireMock.urlMatching("/dc/type/marchepublic:consultation_0/FR/$siret/$reference"))
                .willReturn(WireMock.aResponse().withStatus(204)))

        val consultation = Consultation(reference = reference,
                objet = "mon marche", datePublication = LocalDateTime.now(), dateCloture = LocalDateTime.now(),
                finaliteMarche = FinaliteMarcheType.MARCHE, typeMarche = TypeMarcheType.PUBLIC,
                typePrestation = TypePrestationType.FOURNITURES, departementsPrestation = listOf(6, 83),
                passation = "passation", informatique = true, passe = "motdepasse", emails = listOf("dev@sictiam.fr", "demat@sictiam.fr"),
                enLigne = false, alloti = false, invisible = false, nbLots = 1)

        val entity = restTemplate.postForEntity<String>("/api/marche-public/$siret/consultation", consultation)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(entity.headers["Location"]).isEqualTo(listOf("http://localhost:3000/api/marche-public/$siret/consultation/$reference"))

        restTemplate.delete("/api/marche-public/$siret/consultation/$reference")

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlEqualTo("/dc/type/marchepublic:consultation_0")))
        WireMock.verify(
                WireMock.deleteRequestedFor(
                        WireMock.urlEqualTo("/dc/type/marchepublic:consultation_0/FR/$siret/$reference"))
                            .withHeader("If-Match", EqualToPattern("1")))
    }
}