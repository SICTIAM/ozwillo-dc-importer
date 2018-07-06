package org.ozwillo.dcimporter.web.marchesecurise

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.ozwillo.dcimporter.model.wsdl.marchesecurise.request.GenerateSoapRequest

class DeleteLotSendSoapTest{
    private var login = ""
    private var password = ""
    private var pa = ""
    private var dce = ""
    private var uuid = ""

    @BeforeAll
    fun setup(){
        login = "wsdev-sictiam"
        password = "WS*s1ctiam*"
        pa = "1267898337p8xft"
        dce = "1530514543c6yt3jacnk6x"
        uuid = "15305146545i8p34km21cr"
    }

    @AfterAll
    fun tearDown(){
        login = ""
        password = ""
        pa = ""
        dce = ""
        uuid = ""
    }

    @Test
    fun deleteLot(){
        val soapMessage = GenerateSoapRequest.generateDeleteLotRequest(login, password, pa, dce, uuid)
        println(soapMessage)
        val response = SendSoap.sendSoap(MarcheSecuriseURL.getLotUrl(), soapMessage)
        println(response)
    }
}