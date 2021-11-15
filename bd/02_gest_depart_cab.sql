CREATE OR REPLACE PACKAGE gest_depart AS
	TYPE tcursor IS REF CURSOR;

	PROCEDURE insert_depart
	  (p_nom_dep VARCHAR2,
	  p_loc VARCHAR2);
	PROCEDURE borrar_depart
	  (p_dep_borrar NUMBER,
	  p_dep_nue NUMBER);
	PROCEDURE cambiar_localidad
	  (p_num_dep NUMBER,
	  p_loc VARCHAR2);	
	PROCEDURE visualizar_lista_depart
	   (c_depart OUT tcursor);
	PROCEDURE visualizar_datos_depart
	  (p_num_dep IN OUT NUMBER,
	   p_nom_dep OUT VARCHAR2,
	   p_loc_dep OUT VARCHAR2,
	   p_num_empleados OUT NUMBER);
	PROCEDURE visualizar_datos_depart 
	  (p_nom_dep IN VARCHAR2,
	   c_depart OUT tcursor,
	   p_num_empleados OUT NUMBER);
END gest_depart;
/
-- create public synonym gest_depart for BLANCA.GEST_DEPART;