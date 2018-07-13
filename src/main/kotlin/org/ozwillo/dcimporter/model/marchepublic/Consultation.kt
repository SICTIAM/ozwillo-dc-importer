package org.ozwillo.dcimporter.model.marchepublic

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.ozwillo.dcimporter.util.LocalDateTimeDeserializer
import org.ozwillo.dcimporter.util.LocalDateTimeSerializer
import org.ozwillo.dcimporter.model.datacore.DCBusinessResourceLight
import org.ozwillo.dcimporter.util.DCUtils
import java.io.Serializable
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Consultation(
        val reference: String?,
        val objet: String,
        @JsonSerialize(using = LocalDateTimeSerializer::class)
        @JsonDeserialize(using = LocalDateTimeDeserializer::class)
        val datePublication: LocalDateTime,
        @JsonSerialize(using = LocalDateTimeSerializer::class)
        @JsonDeserialize(using = LocalDateTimeDeserializer::class)
        val dateCloture: LocalDateTime,
        val finaliteMarche: FinaliteMarcheType,
        val typeMarche: TypeMarcheType,
        val typePrestation: TypePrestationType,
        val departementsPrestation: List<Int>,
        val passation: String,
        val informatique: Boolean,
        val passe: String?,
        val emails: List<String>,
        val enLigne: Boolean,
        val alloti: Boolean,
        val invisible: Boolean,
        val nbLots: Int
):Serializable {
    fun toDcObject(baseUri: String, siret: String, reference: String): DCBusinessResourceLight {
        val resourceLight = DCBusinessResourceLight(DCUtils.getUri(baseUri, "marchepublic:consultation_0",
                "FR/$siret/$reference"))
        val organizationUri = DCUtils.getUri(baseUri, "orgfr:Organisation_0", "FR/$siret")
        resourceLight.setStringValue("mpconsultation:organization", organizationUri)
        resourceLight.setStringValue("mpconsultation:reference", reference)
        resourceLight.setStringValue("mpconsultation:objet", objet)
        resourceLight.setDateTimeValue("mpconsultation:datePublication", datePublication)
        resourceLight.setDateTimeValue("mpconsultation:dateCloture", dateCloture)
        resourceLight.setStringValue("mpconsultation:finaliteMarche", finaliteMarche.toString())
        resourceLight.setStringValue("mpconsultation:typeMarche", typeMarche.toString())
        resourceLight.setStringValue("mpconsultation:typePrestation", typePrestation.toString())
        resourceLight.setListValue("mpconsultation:departementsPrestation", departementsPrestation)
        resourceLight.setStringValue("mpconsultation:passation", passation)
        resourceLight.setBooleanValue("mpconsultation:informatique", informatique)
        passe?.let { resourceLight.setStringValue("mpconsultation:passe", passe) }
        resourceLight.setListValue("mpconsultation:emails", emails)
        resourceLight.setBooleanValue("mpconsultation:enLigne", enLigne)
        resourceLight.setBooleanValue("mpconsultation:alloti", alloti)
        resourceLight.setBooleanValue("mpconsultation:invisible", invisible)
        resourceLight.setIntegerValue("mpconsultation:nbLots", nbLots)

        return resourceLight
    }

    fun toDcObject(baseUri: String, siret: String) = toDcObject(baseUri, siret, reference!!)

    companion object {
        fun toConsultation(dcConsultation: DCBusinessResourceLight): Consultation =
                Consultation(reference = dcConsultation.getStringValue("mpconsultation:reference"),
                        objet = dcConsultation.getStringValue("mpconsultation:objet"),
                        datePublication = dcConsultation.getDateValue("mpconsultation:datePublication"),
                        dateCloture = dcConsultation.getDateValue("mpconsultation:dateCloture"),
                        finaliteMarche = FinaliteMarcheType.valueOf(dcConsultation.getStringValue("mpconsultation:finaliteMarche")),
                        typeMarche = TypeMarcheType.valueOf(dcConsultation.getStringValue("mpconsultation:typeMarche")),
                        typePrestation = TypePrestationType.valueOf(dcConsultation.getStringValue("mpconsultation:typePrestation")),
                        departementsPrestation = dcConsultation.getIntListValue("mpconsultation:departementsPrestation"),
                        passation = dcConsultation.getStringValue("mpconsultation:passation"),
                        informatique = dcConsultation.getBooleanValue("mpconsultation:informatique"),
                        passe = dcConsultation.getStringValue("mpconsultation:passe"),
                        emails = dcConsultation.getStringListValue("mpconsultation:emails"),
                        enLigne = dcConsultation.getBooleanValue("mpconsultation:enLigne"),
                        alloti = dcConsultation.getBooleanValue("mpconsultation:alloti"),
                        invisible = dcConsultation.getBooleanValue("mpconsultation:invisible"),
                        nbLots = dcConsultation.getIntValue("mpconsultation:nbLots"))
    }
}

enum class FinaliteMarcheType {
    MARCHE,
    ACCORD,
    DSP,
    AUTRE
}

enum class TypeMarcheType {
    PUBLIC,
    ORDONNANCE2005,
    PRIVE,
    AUTRE
}

enum class TypePrestationType {
    TRAVAUX,
    SERVICES,
    FOURNITURES,
    AUTRES
}
