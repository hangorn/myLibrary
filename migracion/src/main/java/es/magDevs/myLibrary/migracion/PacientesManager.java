package es.magDevs.myLibrary.migracion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

public class PacientesManager {
	Database db;
	Table pacientesTable;
	
	public PacientesManager(Database db) throws IOException {
		this.db = db;
		pacientesTable = db.getTable("PACIENTES");
	}
	
//	public List<PacientesBean> getPacientes() {
//		List<PacientesBean> pacientesList = new ArrayList<PacientesBean>();
////		File f = new File("./listadoSinNumHistoria.txt");
////		BufferedWriter w;
////		try {
////			if(!f.exists()) {
////				f.createNewFile();
////			}
////			w = new BufferedWriter(new FileWriter(f));
////		} catch (IOException e) {
////			return null;
////		}
//		
//		for (Row row : pacientesTable) {
//			PacientesBean p = Rellenador.rellenadorPacientesBean(row);
//			if(p.getNum_historia() != null) {
//				pacientesList.add(p);
//			} 
////			else {
////				String str ="";
////				str+="Nombre: "+p.getNombre()+" "+p.getApellido1()+" "+p.getApellido2()+"\n";
////				str+="Identificador_paciente: "+p.getIdentificador_paciente()+"\n";
////				str+="Identificador: "+p.getIdentificador()+"\n";
////				str+="Numero historia: "+p.getNum_historia()+"\n";
////				str+="Fecha nac: "+p.getFecha_nacimiento()+"\n\n";
////				try {
////					w.write(str);
////				} catch (IOException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
////			}
//		}
////		try {
////			w.flush();
////			w.close();
////		} catch (IOException e) {
////			e.printStackTrace();
////		}
//		return pacientesList;
//	}
//	
//	public DatosPacienteBean getDatosPacienteFromID(int id) {
//		Integer ID = new Integer(id);
//		PacientesBean paciente = getPacienteFromKey("IDENTIFICADOR", ID);
//		if(paciente == null) {
//			return null;
//		}
//		DatosPacienteBean datos = new DatosPacienteBean();
//		datos.setPaciente(paciente);
//		datos.setDatos_clinicos(Listador.listadorDatos_clinicosBean(ID, db));
//		datos.setDatos_clinicos_niños(Listador.listadorDatos_clinicos_niñosBean(ID, db));
//		datos.setDiagnosticos(Listador.listadorDiagnosticoBean(ID, db));
//		datos.setDiagnosticos_niños(Listador.listadorDiagnostico_niñosBean(ID, db));
//		datos.setEstados(Listador.listadorEstadosBean(ID, db));
//		datos.setPr_espirometria(Listador.listadorPr_espirometriaBean(ID, db));
//		datos.setPr_gasometriaBean(Listador.listadorPr_gasometriaBean(ID, db));
//		datos.setPr_poligrafiaBean(Listador.listadorPr_poligrafiaBean(ID, db));
//		datos.setPr_polisomnografiaBean(Listador.listadorPr_polisomnografiaBean(ID, db));
//		datos.setPr_tlmsBean(Listador.listadorPr_tlmsBean(ID, db));
//		datos.setTratamientos(Listador.listadorTratamientoBean(ID, db));
//		datos.setTratamientos_niños(Listador.listadorTratamiento_niñosBean(ID, db));
//		datos.setVisitas_enfermeria(Listador.listadorVisitas_enfermeriaBean(ID, db));
//		datos.setVisitas_medicas(Listador.listadorVisitas_medicasBean(ID, db));
//		datos.setVisitas_medicas_niños(Listador.listadorVisitas_medicas_niñosBean(ID, db));
//		return datos;
//	}
//	
//	public DatosPacienteBean getDatosPacienteFromNum_historia(String num_historia) {
//		PacientesBean paciente = getPacienteFromKey("NUM_HISTORIA", num_historia);
//		if(paciente == null) {
//			return null;
//		}
//		Integer ID = paciente.getIdentificador();
//		return getDatosPacienteFromID(ID);
//	}
//	
//	private PacientesBean getPacienteFromKey(String key, Object value) {
//		Table table;
//		try {
//			table = db.getTable("PACIENTES");
//			Cursor cursor = CursorBuilder.createCursor(table);
//			boolean found = cursor.findFirstRow(Collections.singletonMap(key, value));
//			if (found) {
//				Row row = cursor.getCurrentRow();
//				return Rellenador.rellenadorPacientesBean(row);
//			} else {
//				return null;
//			}
//		} catch (Exception e) {
//			return null;
//		}
//	}
}
