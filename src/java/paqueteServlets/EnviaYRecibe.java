package paqueteServlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/EnviaYRecibe")
public class EnviaYRecibe extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private LinkedHashMap<String, String> arrayPaises = new LinkedHashMap<String, String>(); //array asociativo

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	//Carga del array asociativo
        
        arrayPaises.put("ES", "España");
        arrayPaises.put("FR", "Francia");
        arrayPaises.put("IT", "Italia");
        arrayPaises.put("PT", "Portugal");

        // Recepción de parámetros
        String nombre = request.getParameter("nombre");
        String clave = request.getParameter("clave");
        String genero = request.getParameter("genero");
        String fechaNac = request.getParameter("fechaNac");
        String[] paises = request.getParameterValues("paises[]");
        String acepto = request.getParameter("acepto");
        String comentarios = request.getParameter("comentarios");
        String foto = request.getParameter("foto");

        PrintWriter out = response.getWriter();

        // 1.Creación del formulario vacío inicial y lógica de negocio
        if (nombre == null) {   // solo ocurre en la primera llamada
            out.println("<h1>EnviaYRecibe</h1>");
            out.println("<fieldset>\n" + "<legend>FORMULARIO DE ALTA</legend> <br />");
            out.println("<form name=\"f1\" action=\"EnviaYRecibe\" method=\"get\">");
            out.println("<label for=\"nombre\">Nombre </label> <input type=\"text\" name=\"nombre\" /> <br /> <br />");
            out.println("<label for=\"clave\">Clave </label><input type=\"password\" name=\"clave\" /> <br /> <br />");
            out.println("<label>Género: </label> ");
            out.println("<label>Mujer</label><input type=\"radio\" value=\"F\" name=\"genero\" />");
            out.println("<label>Hombre</label><input type=\"radio\" value=\"M\" name=\"genero\" /> <br /><br />");
            out.println("<label for=\"fechaNac\">Fecha de nacimiento </label><input type=\"text\" name=\"fechaNac\" /> (dd/mm/aaaa) <br /> <br />");
            out.println("<label>País/es de origen: </label> <br />");
            out.println("<select name=\"paises[]\" multiple=\"multiple\">");
            out.println("<option value=\"ES\">España</option>");
            out.println("<option value=\"FR\">Francia</option>");
            out.println("<option value=\"IT\">Italia</option>");
            out.println("<option value=\"PT\">Portugal</option>");
            out.println("</select> <br /> <br />");
            out.println("<label for=\"acepto\">Acepto las condiciones del alta en el servicio</label>");
            out.println("<input type=\"checkbox\" name=\"acepto\" value=\"OK\" /> <br /> <br />");
            out.println("<label for=\"comentarios\">Comentarios</label> <br />");
            out.println("<textarea name=\"comentarios\" rows=\"5\" cols=\"30\">Escriba sus comentarios...</textarea>  <br /> <br />");
            out.println("<label for=\"foto\">Fotografía: </label><input type=\"file\" name=\"foto\" /> <br /><br />");
            out.println("<input type=\"submit\" value=\"Enviar\" />");
            out.println("<input type=\"reset\" value=\"Reset\" />");
            out.println("</form>\n" + "</fieldset>");
        }

        // Lógica de negocio
        String errores = "";  // cadena que va recogiendo los posibles erorres del formulario
        if (nombre.equals("")) {
            errores += "Rellena el nombre <br/>";
        }

        if (clave.length() < 6 || clave.length() > 12) {
            errores += "Clave incorrecta <br/>";
        }

        if (genero == null) {       //Los radiobutton vacíos son null
            errores += "Rellena el género <br/>"; 
        }

        if (fechaNac.equals("") || fechaNac.length()!=10) {
            errores += "Rellena la fecha de nacimiento <br/>";
        } else if (!mayorDeEdad(fechaNac) ) {
            errores += "Debes ser mayor de edad para realizar el alta <br />";
        }

        if (paises == null) {
            errores += "Rellena el país/es de origen <br/>";
        }

        if (acepto == null) {
            errores += "Debes aceptar las condiciones de alta en el servicio <br/>";
        }

        // 2.Creación del formulario con errores
        if (errores.length() != 0) {
            out.println("<html>");
            out.println("<body>");
            out.println("<h3>Errores en el Formulario </h3>");
            out.println("<p style=\"color:red\">" + errores + "</p>");
            out.println("<fieldset>\n" + "<legend>FORMULARIO DE ALTA</legend> <br />");
            out.println("<form name=\"f1\" action=\"EnviaYRecibe\" method=\"get\" >");
            
            out.println("Nombre: <input type=\"text\" name=\"nombre\" value=\"" + nombre + "\" /> <br /> <br /> ");
            out.println("Clave: <input type=\"password\" name=\"clave\" /> <br /> <br />"); //La clave no se regenera
            out.println(generaButtonGenero(genero) + "<br /> <br />");
            out.println("Fecha Nacimiento: <input type=\"text\" name=\"fechaNac\" value=\"" + fechaNac + "\"> (dd/mm/aaaa)<br /> <br />");
            out.println(generaSelectPaises(arrayPaises, paises) + "<br />");
            out.println("Acepto las condiciones del alta en el servicio: "
                    + "<input type=\"checkbox\" name=\"acepto\" value=\"OK\" /> <br /> <br /> ");
            out.println("Comentarios: <br /> <textarea name=\"comentarios\" rows=\"5\" cols=\"30\"/>" + comentarios 
                    + "</textarea> <br /> <br /> ");
            out.println("Foto: <input type=\"file\" name=\"foto\" /> <br /> <br />");
            
            out.println("<input type=\"submit\" value=\"Enviar\" />");
            out.println("<input type=\"reset\" value=\"Reset\" />");
            out.println("</form>\n" +  "</fieldset>");
            out.println("</body>");
            out.println("</html>");
        } 

        // 3.Creación del formulario enviado con éxito
        else {
            out.println("<h1>Formulario enviado con éxito</h1>" + "<br/>");
            out.println("Nombre = " + nombre + "<br />");
            out.println("Clave = " + clave + "<br />");
            out.println("Género = " + genero + "<br />");
            out.println("Fecha de nacimiento = " + fechaNac + "<br />");
            out.println("País/es de origen = " + Arrays.toString(paises) + "<br />");
            out.println("Acepto = " + acepto + "<br />");
            out.println("Comentarios = " + comentarios + "<br />");
            out.println("Foto = " + foto + "<br />");

        }
    }

    // Funciones para regenerar el formulario con errores
    
    protected String generaButtonGenero(String _genero) {
        String cadena = "Género: ";
        if (_genero==null) {
            cadena += "<label>Mujer</label><input type=\"radio\" name=\"genero\" value=\"F\" />";
            cadena += "<label>Hombre</label><input type=\"radio\" name=\"genero\" value=\"M\" />";
        } else if (_genero.equals("F")){
            cadena += "<label>Mujer</label><input type=\"radio\" name=\"genero\" value=\"F\" checked=\"checked\"/>";
            cadena += "<label>Hombre</label><input type=\"radio\" name=\"genero\" value=\"M\" />";
        } else {
            cadena += "<label>Mujer</label><input type=\"radio\" name=\"genero\" value=\"F\" />";
            cadena += "<label>Hombre</label><input type=\"radio\" name=\"genero\" value=\"M\" checked=\"checked\"/>";
        }
        return cadena;
    }
    

    protected Boolean mayorDeEdad(String _fechaNac) {
        
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaHoy = new Date();
        String cadenaHoy = formatoFecha.format(fechaHoy);
        
        String[] dat1 = _fechaNac.split("/");
        String[] dat2 = cadenaHoy.split("/");
        int años = Integer.parseInt(dat2[2]) - Integer.parseInt(dat1[2]);
        int mes = Integer.parseInt(dat2[1]) - Integer.parseInt(dat1[1]);
        if (mes < 0) {
            años = años - 1;
        } else if (mes == 0) {
            int dia = Integer.parseInt(dat2[0]) - Integer.parseInt(dat1[0]);
            if (dia > 0) {
                años = años - 1;
            }
        }
        
        if(años<18)
            return false;
        else
            return true;
    }


     protected String generaSelectPaises (LinkedHashMap<String, String> arrayPaises,  String[] _paises) {
        String cadena = "País/es de origen: <br /> <select name=\"paises[]\" multiple=\"multiple\">";
        int numPaises = 0;
        int i=0;
        int cont=0;
        
        if (_paises!=null) {
            numPaises= _paises.length;  // Si el parámetro paises recibido no es nulo se calcula su longitud
        }
        System.out.println("num"+numPaises);
        Iterator<String> iterador = arrayPaises.keySet().iterator();
        while (iterador.hasNext()) {
            String clave = (String)iterador.next();
            String valor = (String)arrayPaises.get(clave);
            if ( i<numPaises && _paises[i].equals(clave)) {                
                cadena += "<option value=\"" + clave + "\"  selected=\"selected\">" + valor + "</option>";
                i++;
            } else {
                cadena += "<option value=\"" + clave + "\">" + valor + "</option>";
            }
            cont++;
            System.out.println(cont+" "+i+clave+valor); // para control interno
        }
        cadena +="</select>";
        return cadena;
    }
}
