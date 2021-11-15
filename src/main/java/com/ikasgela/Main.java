package com.ikasgela;

import oracle.jdbc.OracleTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class Main {

    private static final Logger logger = LogManager.getLogger();
    private static Connection conexion = null;

    public static void main(String[] args) {

        // Conectar a la base de datos
        System.out.println("--- Conexión a Oracle --------------------------");
        conectar();
        System.out.println("------------------------------------------------\n");

        // Si no hay conexión, no podemos continuar con los ejemplos
        if (conexion == null) {
            return;
        }

        // Consulta simple
        System.out.println("--- Consulta simple ----------------------------");
        consultaSimple();
        System.out.println("------------------------------------------------\n");

        // Consulta preparada
        System.out.println("--- Consulta preparada -------------------------");
        consultaPreparada();
        System.out.println("------------------------------------------------\n");

        // Ejemplos de procedimientos almacenados (sin empaquetar)

        // Llamada a procedimiento almacenado insert_depart1
        System.out.println("--- Procedimiento almacenado: Insertar ---------");
        llamadaInsertDepart1();
        System.out.println("------------------------------------------------\n");

        // Llamada a procedimiento almacenado visualizar_lista_depart1
        System.out.println("--- Proc. almacenado: Visualizar lista ---------");
        llamadaVisualizarListaDepart1();
        System.out.println("------------------------------------------------\n");

        // Ejemplos de procedimientos empaquetados en gest_depart

        // Llamada a procedimiento empaquetado gest_depart.insert_depart
        System.out.println("--- Procedimiento empaquetado: Insertar --------");
        llamadaEmpaquetadoInsertDepart();
        System.out.println("------------------------------------------------\n");

        // Llamada a procedimiento empaquetado gest_depart.visualizar_lista_depart
        System.out.println("--- Proc. empaquetado: Visualizar lista --------");
        llamadaEmpaquetadoVisualizarLista();
        System.out.println("------------------------------------------------\n");

        // Llamada a procedimiento empaquetado gest_depart.visualizar_datos_depart
        System.out.println("--- Proc. empaquetado: Visualizar datos (I) ----");
        llamadaEmpaquetadoVisualizarDatosIndices();
        System.out.println("------------------------------------------------\n");

        // Llamada a procedimiento empaquetado gest_depart.visualizar_datos_depart
        System.out.println("--- Proc. empaquetado: Visualizar datos (N) ----");
        llamadaEmpaquetadoVisualizarDatosNombres();
        System.out.println("------------------------------------------------\n");

        // Cerrar la conexión
        System.out.println("--- Desconexión de Oracle ----------------------");
        desconectar();
        System.out.println("------------------------------------------------\n");
    }

    public static void conectar() {
        try {

            // Cadena de conexión
            String servidor = Configuracion.leer("DB_HOST");
            String puerto = Configuracion.leer("DB_PORT");
            String bd = Configuracion.leer("DB_DATABASE");
            String login = Configuracion.leer("DB_USERNAME");
            String password = Configuracion.leer("DB_PASSWORD");
            String url = "jdbc:oracle:thin:@" + servidor + ":" + puerto + ":" + bd;

            // Establecimiento de conexión
            conexion = DriverManager.getConnection(url, login, password);

            logger.info("Conexión abierta");

        } catch (SQLException e) {
            logger.fatal(e);
        }
    }

    public static void consultaSimple() {
        try {

            Statement stmt = conexion.createStatement();

            ResultSet rset = stmt.executeQuery("select * from SYS.V_$VERSION");
            while (rset.next()) {
                System.out.println(rset.getString(1));
            }

            stmt.close();

        } catch (SQLException e) {
            logger.error(e);
        }
    }

    public static void consultaPreparada() {
        try {

            PreparedStatement st = conexion.prepareStatement("INSERT INTO DEPART VALUES (?, ?, ?)");

            st.setInt(1, 50);
            st.setString(2, "MISTERIOS");
            st.setString(3, "AREA51");

            int filas = st.executeUpdate();
            System.out.println("Filas afectadas: " + filas);

        } catch (SQLException e) {
            logger.error(e);
        }
    }

    public static void llamadaInsertDepart1() {
        try {

            //Creamos el statement
            String sql = "{ call insert_depart1(?,?) }";
            CallableStatement cs = conexion.prepareCall(sql);

            // Cargamos los parametros de entrada IN
            cs.setString(1, "INFORMÁTICA");
            cs.setString(2, "VITORIA");

            // Ejecutamos la llamada
            cs.execute();

            logger.info("Procedimiento ejecutado");

        } catch (SQLException e) { // Controlamos los errores que queremos sacar
            if (e.getErrorCode() == 20001) {
                // Usamos e.getMessage() para sacar el mensaje de RAISE_APPLICATION_ERRORS
                logger.error(e.getMessage());
            }
        }
    }

    public static void llamadaVisualizarListaDepart1() {
        try {

            // Creamos el statement
            String sql = "{ call visualizar_lista_depart1(?) }";
            CallableStatement cs = conexion.prepareCall(sql);

            // Cargamos los parametros de entrada OUT
            cs.registerOutParameter(1, OracleTypes.CURSOR);

            // Ejecutamos la llamada
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                System.out.println(rs.getString("LOC"));
            }
            rs.close();

            logger.debug("Procedimiento ejecutado");

        } catch (SQLException e) {
            logger.error(e);
        }
    }

    public static void llamadaEmpaquetadoInsertDepart() {
        try {

            // Creamos el statement
            String sql = "{ call gest_depart.insert_depart(?,?) }";
            CallableStatement cs = conexion.prepareCall(sql);

            // Cargamos los parametros de entrada IN
            cs.setString(1, "PROGRAMACION");
            cs.setString(2, "VITORIAGASTEIZ");

            // Ejecutamos la llamada
            cs.execute();

            logger.info("Procedimiento ejecutado");

        } catch (SQLException e) {
            // Controlamos los errores que queremos sacar
            if (e.getErrorCode() == 20001) {
                // Usamos e.getMessage() para sacar el mensaje de RAISE_APPLICATION_ERRORS
                logger.error(e.getMessage());
            }
        }
    }

    public static void llamadaEmpaquetadoVisualizarLista() {
        try {
            // Creamos el statement
            String sql = "{ call gest_depart.visualizar_lista_depart(?) }";
            CallableStatement cs = conexion.prepareCall(sql);

            // Cargamos los parametros de entrada OUT
            cs.registerOutParameter(1, OracleTypes.CURSOR);

            // Ejecutamos la llamada
            cs.execute();

            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                System.out.println(rs.getString("LOC"));
            }
            rs.close();

            logger.debug("Procedimiento ejecutado");

        } catch (SQLException e) {
            logger.error(e);
        }
    }

    public static void llamadaEmpaquetadoVisualizarDatosIndices() {
        try {

            // Utilizando índices de parámetros
            String sql = "{ call gest_depart.visualizar_datos_depart(?,?,?,?) }";
            CallableStatement cs = conexion.prepareCall(sql);

            // Cargamos los parametros de entrada IN
            cs.setInt(1, 40);

            // Registramos los parametros de salida OUT
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.registerOutParameter(3, Types.VARCHAR);
            cs.registerOutParameter(4, Types.INTEGER);

            // Ejecutamos la llamada
            cs.execute();

            int dept_no = cs.getInt(1);
            System.out.println("Dept NO: " + dept_no);
            String nomdept = cs.getString(2);
            System.out.println("Nomdep: " + nomdept);
            String loc = cs.getString(3);
            System.out.println("Loc: " + loc);

            logger.debug("Procedimiento ejecutado");

        } catch (SQLException e) {
            logger.error(e);
        }
    }

    public static void llamadaEmpaquetadoVisualizarDatosNombres() {
        try {

            // Utilizando nombres de parámetros
            String sql = "{ call gest_depart.visualizar_datos_depart(?,?,?,?) }";
            CallableStatement cs = conexion.prepareCall(sql);

            // Cargamos los parametros de entrada IN
            cs.setInt("p_num_dep", 40);

            // Registramos los parametros de salida OUT
            cs.registerOutParameter("p_nom_dep", Types.VARCHAR);
            cs.registerOutParameter("p_loc_dep", Types.VARCHAR);
            cs.registerOutParameter("p_num_empleados", Types.INTEGER);

            // Ejecutamos la llamada
            cs.execute();

            int dept_no = cs.getInt("p_num_dep");
            System.out.println("Dept NO: " + dept_no);
            String nomdept = cs.getString("p_nom_dep");
            System.out.println("Nomdep: " + nomdept);
            String loc = cs.getString("p_loc_dep");
            System.out.println("Loc: " + loc);

            logger.debug("Procedimiento ejecutado");

        } catch (SQLException e) {
            logger.error(e);
        }
    }

    public static void desconectar() {
        try {
            conexion.close();
            logger.info("Conexión cerrada");
        } catch (SQLException e) {
            logger.error(e);
        }
    }
}
