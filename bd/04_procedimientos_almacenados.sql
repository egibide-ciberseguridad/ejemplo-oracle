CREATE OR REPLACE PROCEDURE insert_depart1 (
	p_nom_dep VARCHAR2,
	p_loc VARCHAR2)
 AS
	v_ultimo_dep DEPART.DEPT_NO%TYPE;
	e_nombre_repetido EXCEPTION;
	v_nom_dep depart.DNOMBRE%TYPE; 
	BEGIN
	  -- Comprobar dpt repetido(Puede disparar NO_DATA_FOUND)
	  SELECT dnombre INTO v_nom_dep FROM depart
		WHERE upper(dnombre) = UPPER(p_nom_dep);
      RAISE e_nombre_repetido;  
	EXCEPTION
	  WHEN NO_DATA_FOUND THEN
	    -- Calcular el número de departamento e insertar 
    	SELECT NVL2(MAX(DEPT_NO),MAX(DEPT_NO)+10,10) INTO v_ultimo_dep FROM DEPART;
        -- Como hay una funcion de grupo no funciona la exception NO_DATA_FOUND
        INSERT INTO DEPART VALUES (v_ultimo_dep, p_nom_dep,p_loc);
      /* Otra forma de implementar el control tabla vacia. En lugar de NVL2 usar IF
      IF v_ultimo_dep IS NULL
      THEN
      	--Si la tabla departamento esta vacia, es decir, no hay ningun departamento
      	INSERT INTO DEPART VALUES (10,upper(p_nom_dep),upper(p_loc));
    	ELSE
        INSERT INTO DEPART VALUES (v_ultimo_dep+10, p_nom_dep,p_loc);
      END IF;
      */
	  WHEN e_nombre_repetido THEN
	    RAISE_APPLICATION_ERROR ('-20001','Err. Nombre de departamento duplicado');		
        --Fin comprobación de departamento repetido 
      WHEN OTHERS THEN
      	RAISE_APPLICATION_ERROR('-20999','Error desconocido');
END insert_depart1;
/
-- **************************************************************
CREATE OR REPLACE PROCEDURE borrar_depart1
	  (p_dep_borrar NUMBER,
	  p_dep_nue NUMBER)
 AS
  v_dept_no depart.dept_no%TYPE;
 BEGIN 
 	-- Comprobar que existe el departamento a borrar
   SELECT dept_no INTO v_dept_no
   FROM depart 
 		WHERE dept_no=p_dep_borrar;
 	SELECT dept_no INTO v_dept_no
    FROM depart 
 		WHERE dept_no=p_dep_nue;
 	UPDATE emple SET dept_no = p_dep_nue
  	  WHERE DEPT_NO=p_dep_borrar;
	DELETE FROM depart WHERE dept_no  = p_dep_borrar;
 EXCEPTION
 	WHEN NO_DATA_FOUND THEN
 		RAISE_APPLICATION_ERROR('-20011','Err. Nombre Departamento no encontrado');
 	WHEN OTHERS THEN
      	RAISE_APPLICATION_ERROR('-20999','Error desconocido');	
END borrar_depart1; 
/
-- **************************************************************
CREATE OR REPLACE PROCEDURE cambiar_localidad1(
	p_num_dep NUMBER,
	p_loc VARCHAR2)
 AS
	e_dept_no_encontrado EXCEPTION;
 BEGIN
	UPDATE depart
		SET LOC=p_loc
		WHERE dept_no=p_num_dep;
	IF SQL%NOTFOUND
	THEN
		RAISE e_dept_no_encontrado;
	END IF;
 EXCEPTION
	WHEN e_dept_no_encontrado THEN
		RAISE_APPLICATION_ERROR('-20011','Err Nombre Departamento no encontrado');
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR('-20999','Error desconocido');
END cambiar_localidad1;
/
-- ************************************************************
CREATE OR REPLACE PACKAGE pkgLeerDepart
AS
	TYPE tcursor IS REF CURSOR;
END;
/
-- ************************************************************
CREATE OR REPLACE PROCEDURE visualizar_lista_depart1
	   (c_depart OUT pkgLeerDepart.tcursor)
AS

BEGIN
	 OPEN c_depart FOR
		SELECT dept_no,dnombre,loc FROM depart;
END visualizar_lista_depart1;
/
-- *************************************************************
CREATE OR REPLACE PROCEDURE visualizar_datos_depart1
	  (p_num_dep IN OUT NUMBER,
	   p_nom_dep OUT VARCHAR2,
	   p_loc_dep OUT VARCHAR2,
	   p_num_empleados OUT NUMBER)
 AS

 BEGIN
	SELECT dept_no, dnombre, loc INTO p_num_dep,p_nom_dep,p_loc_dep 
		FROM depart
		WHERE DEPT_NO=p_num_dep;

	SELECT NVL(COUNT(*),0) INTO p_num_empleados
		FROM EMPLE
		WHERE DEPT_NO=p_num_dep;
 EXCEPTION
	WHEN NO_DATA_FOUND THEN
	  RAISE_APPLICATION_ERROR('-20021','Err Cod. Departamento no encontrado');
 END visualizar_datos_depart1;
/
-- ************************************************************* 

CREATE OR REPLACE FUNCTION  buscar_depart_por_nombre
	  (p_nom_dep VARCHAR2)
 RETURN NUMBER
 AS
	p_num_dep depart.dept_no%TYPE;
 BEGIN
	SELECT dept_no INTO p_num_dep FROM depart
WHERE DNOMBRE = p_nom_dep;
	RETURN p_num_dep;
 EXCEPTION
	WHEN NO_DATA_FOUND THEN
		RAISE_APPLICATION_ERROR('-20011','Err Nombre Departamento no encontrado');
 END buscar_depart_por_nombre;
 -- ******************


 
 