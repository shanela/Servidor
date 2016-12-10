/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.distri.servidor;

/**
 *
 * @author shane
 */
import ec.edu.distri.clientejava.protocolo.Mensaje;
import static ec.edu.distri.clientejava.protocolo.Mensaje.ID_MENSAJE_ACCEDERSERVICIO;
import static ec.edu.distri.clientejava.protocolo.Mensaje.ID_MENSAJE_CONSULTAFACTURA;
import static ec.edu.distri.clientejava.protocolo.Mensaje.ID_MENSAJE_LOGIN;
import ec.edu.distri.clientejava.protocolo.MensajeRP;
import ec.edu.distri.clientejava.protocolo.MensajeRQ;
import ec.edu.distri.clientejava.protocolo.model.Plan;
import ec.edu.distri.clientejava.protocolo.model.ServicioAdicional;
import ec.edu.distri.clientejava.protocolo.servicio.AccederServicioRP;
import ec.edu.distri.clientejava.protocolo.servicio.AccederServicioRQ;
import ec.edu.distri.clientejava.protocolo.servicio.ConsultarFacturaRP;
import ec.edu.distri.clientejava.protocolo.servicio.ConsultarFacturaRQ;
import ec.edu.distri.clientejava.protocolo.servicio.LoginUsuarioRP;
import ec.edu.distri.clientejava.protocolo.servicio.LoginUsuarioRQ;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;

public class AppSocketSession extends Thread {

    private static Integer global = 0;
    private PrintWriter output;
    private BufferedReader input;
    private Socket socket;

    private Integer id;

    public AppSocketSession(Socket socket) throws IOException {

        this.id = AppSocketSession.global++;
        this.socket = socket;
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {

            String userInput;

            while ((userInput = input.readLine()) != null) {

                if ("FIN".equalsIgnoreCase(userInput)) {
                    break;
                }
                System.out.println("Hilo: " + this.id + " Mensaje recibido: " + userInput);
                MensajeRQ msj = new MensajeRQ();

                if (msj.build(userInput)) {
                    switch (msj.getCabecera().getIdMensaje()) {
                        case Mensaje.ID_MENSAJE_LOGIN: {
                            LoginUsuarioRQ aut = (LoginUsuarioRQ) msj.getCuerpo();
                            LoginUsuarioRP resp = new LoginUsuarioRP();
                            if (aut.getUs().getNombre().equals("admin") && (aut.getUs().getContraseña().equals("admin"))) {
                                resp.setResultado("1");
                            } else {
                                resp.setResultado("2");
                            }
                            MensajeRP msjrespuesta = new MensajeRP("core", Mensaje.ID_MENSAJE_LOGIN);
                            msjrespuesta.setCuerpo(resp);
                            output.write(msjrespuesta.asTexto() + "\n");
                            System.out.println("respuesta=" + msjrespuesta.asTexto());
                            output.flush();
                            break;
                        }
                        case Mensaje.ID_MENSAJE_ACCEDERSERVICIO: {
                            AccederServicioRQ aut = (AccederServicioRQ) msj.getCuerpo();
                            AccederServicioRP resp = new AccederServicioRP();
                          
                            Plan plan = new Plan("Oro","Todos los canales ofertados por DirecTv");
                            ServicioAdicional servicioAdicional = new ServicioAdicional("Fox Deportes","Canal de futbol",new BigDecimal(5.3));
                            
                            
                            if (aut.getUsuario().equals("admin")) {
                                resp.setPlan(plan);
                                resp.setServicioAdicional(servicioAdicional);
                                System.out.println("Aqui recibes los planes del usuaerio y tambien los servicios adicionales");
                            } else {
                                // resp.setResultado("2");
                                System.out.println("upss algo salio mal");

                            }
                            MensajeRP msjrespuesta = new MensajeRP("core", Mensaje.ID_MENSAJE_ACCEDERSERVICIO);
                            msjrespuesta.setCuerpo(resp);
                            output.write(msjrespuesta.asTexto() + "\n");
                            System.out.println("respuesta=" + msjrespuesta.asTexto());
                            output.flush();
                            break;
                        }
                        case Mensaje.ID_MENSAJE_CONSULTAFACTURA: {

                            ConsultarFacturaRQ aut = (ConsultarFacturaRQ) msj.getCuerpo();
                            ConsultarFacturaRP resp = new ConsultarFacturaRP();
                            if (aut.getUsuario().equals("admin")) {
                                resp.getCliente();
                                resp.getDetalleFactura();
                            } else {

                            }

                            MensajeRP msjrespuesta = new MensajeRP("core", Mensaje.ID_MENSAJE_CONSULTAFACTURA);
                            msjrespuesta.setCuerpo(resp);
                            output.write(msjrespuesta.asTexto() + "\n");
                            System.out.println("respuesta=" + msjrespuesta.asTexto());

                            output.flush();
                            break;
                        }
                        default:
                            System.out.println("Mensaje no soportado");
                            break;
                    }

                } else {
                    System.out.println("No se pudo construir el  mensaje entrante");
                }
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
