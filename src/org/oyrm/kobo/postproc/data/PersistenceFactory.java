/**
 *	PersistenceFactory is an Abstract superclass designed according to
 *	the AbstractFactory pattern (GoF) and from from the Core J2EE 
 *	Design pattern Data Access Object, architectural details can be found here 
 * @link http://java.sun.com/blueprints/corej2eepatterns/Patterns/DataAccessObject.html 
 *
 *	Subclasses may override the abstract
 *	methods in order to provide persisitent storage data access objects to
 *	clients.
 */
package org.oyrm.kobo.postproc.data;

/**
 * The PersistenceFactory should be extended for different persistent mediums
 * @author Gary Hendrick
 *
 */
public abstract class PersistenceFactory {
	public static final int CSV = 0;
	public static final int XML = 1;
	public static final int SERIALIZED = 2;
	
	/**
	 * 
	 * @return
	 */
	public static KoboConnection createConnection() {
		return new BaseKoboConnection();
	}
	
	/**
	 * 
	 * @return
	 */
	public abstract SurveyRecordDAO getSurveyRecordDAO();
	
	/**
	 * Factory Method. The calling method need only change the factoryID to 
	 * select a different storage medium. in truth, only the CSV factory is currently
	 * valid
	 * @param factoryID the concrete implementation of factory to be returned
	 * 	is specified by the factoryID. An invalid factoryID will return CSV
	 * @return PersistenceFactory implementation for the appropriate type
	 */
	public static PersistenceFactory getPersistenceFactory(int factoryID) {
		switch (factoryID) {
			case CSV :
				return new CSVFactory();
			case XML :
				//return new XMLFactory();
			case SERIALIZED :
				//return new SerialFactory();
			default:
				return new CSVFactory();
		}
	}
}
