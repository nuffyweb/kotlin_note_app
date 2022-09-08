package persistence

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver
import note.model.Note
import java.io.File
import java.io.FileWriter
import java.nio.file.Paths


class JSONSerializer(private val file: String) : Serializer {
    private val mapper = jacksonObjectMapper()
    @Throws(Exception::class)
    override fun read(): Any {
        val jsonString: String = File(file).readText(Charsets.UTF_8)
        val obj:List<Note> = mapper.readValue<List<Note>>(jsonString)
        return obj
    }

    @Throws(Exception::class)
    override fun write(obj: Any?) {
        mapper.writeValue(Paths.get(file).toFile(), obj)
    }
}