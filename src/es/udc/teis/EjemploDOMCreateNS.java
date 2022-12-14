/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.udc.teis;

import es.udc.teis.model.Version;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author maria
 */
public class EjemploDOMCreateNS {

    private static final String VERSIONES_TAG = "versiones";
    private static final String VERSION_TAG = "version";
    private static final String VERSION_Q_TAG = "v:version";
    private static final String VERSION_NOMBRE_TAG = "nombre";
    private static final String VERSION_API_TAG = "api";
    private static final String VERSION_ATT_NUMERO = "numero";

    private static final String VERSIONES_OUTPUT_FILE = Paths.get("src", "docs", "versiones_ns_output.xml").toString();

    private static final String VERSIONES_NS_URI = "http://www.versiones.com";
    private static final String VERSIONES_NS_URI_PREFIX = "v";

    public static void main(String[] args) {
        try {

            ArrayList<Version> versions = crearVersions();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            DOMImplementation implementation = builder.getDOMImplementation();
            //DTDs no soportan namespaces => docType es null

            //Crea un document con un elmento raiz. Si a??adimos aqu?? el namespace, se a??adir?? al elemento ra??z con el prefijo que se indique en el 2?? argumento.
            //Si no se indica prefijo, ser?? el ns por defecto
            Document document = implementation.createDocument(null, VERSIONES_TAG, null);

            //Si no se necesita DOCTYPE se podr??a llamar a createDocument con el tercer par??metro a null
            //Document document = implementation.createDocument(null, VERSIONES_TAG, docType);
            //Obtenemos el elemento ra??z
            Element root = document.getDocumentElement();
            //Para a??adir m??s de un namespace 
            root.setAttribute("xmlns:"+VERSIONES_NS_URI_PREFIX, VERSIONES_NS_URI);
         
         
         
         

            //Existe otra posibilidad para la creaci??n de un document totalmente vac??o, al que hay que a??adirle un elemento ra??z:
//            //Crear un nuevo documento XML VAC??O
//            Document document = builder.newDocument();
//            //Crear el nodo ra??z y a??adirlo al documento
//            Element root = document.createElement(VERSIONES_TAG);
//            document.appendChild(root);
            int contador = 1;
            for (Version version : versions) {
                //Para ejemplificar c??mo pueden convivir etiquetas calificadas con no calificadas usamos
                // el criterio: "los elementos de la lista con ??ndice impar ir??n calificadas, los de ??ndice par no:"

                boolean par = (contador % 2 == 0);
                Element eVersion = null;
                if (par) {
                    eVersion = document.createElement(VERSION_TAG);
                } else {
                    eVersion = document.createElementNS(VERSIONES_NS_URI, VERSION_Q_TAG);
                }

                eVersion.setAttribute(VERSION_ATT_NUMERO, String.valueOf(version.getNumero()));

                addElementConTexto(document, eVersion, VERSION_NOMBRE_TAG, version.getNombre());
                addElementConTexto(document, eVersion, VERSION_API_TAG, String.valueOf(version.getApi()));

                root.appendChild(eVersion);
                contador++;
            }

            //Para generar un documento XML con un objeto Document
            //Generar el tranformador para obtener el documento XML en un fichero
            TransformerFactory fabricaTransformador = TransformerFactory.newInstance();
            //Espacios para indentar cada l??nea
            fabricaTransformador.setAttribute("indent-number", 4);
            Transformer transformador = fabricaTransformador.newTransformer();
            //Insertar saltos de l??nea al final de cada l??nea
            //https://docs.oracle.com/javase/8/docs/api/javax/xml/transform/OutputKeys.html
            transformador.setOutputProperty(OutputKeys.INDENT, "yes");

            //Si se quisiera a??adir el <!DOCTYPE>:
            // transformador.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, docType.getSystemId());
            //El origen de la transformaci??n es el document
            Source origen = new DOMSource(document);
            //El destino ser?? un stream a un fichero 
            Result destino = new StreamResult(VERSIONES_OUTPUT_FILE);
            transformador.transform(origen, destino);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            System.err.println("Ha ocurrido una exception: " + e.getMessage());
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            System.err.println("Ha ocurrido una exception: " + e.getMessage());
        } catch (TransformerException e) {
            e.printStackTrace();
            System.err.println("Ha ocurrido una exception: " + e.getMessage());
        }

    }

    private static ArrayList<Version> crearVersions() {
        ArrayList<Version> versions = new ArrayList<>();

        Version versionA = new Version(1, "nombreA", 2);

        Version versionB = new Version(1.5, "nombreB", 3);
        Version versionC = new Version(2, "nombreC", 4);
        versions.add(versionA);
        versions.add(versionB);
        versions.add(versionC);

        return versions;
    }

    private static void addElementConTexto(Document document, Node padre, String tag, String text) {
        //Creamos un nuevo nodo de tipo elemento desde document
        Node node = document.createElement(tag);
        //Creamos un nuevo nodo de tipo texto tambi??n desde document
        Node nodeTexto = document.createTextNode(text);
        //a??adimos a un nodo padre el nodo elemento
        padre.appendChild(node);
        //A??adimos al nodo elemento su nodo hijo de tipo texto
        node.appendChild(nodeTexto);
    }

}
