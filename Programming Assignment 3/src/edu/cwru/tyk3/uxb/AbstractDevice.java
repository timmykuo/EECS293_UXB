package edu.cwru.tyk3.uxb;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public abstract class AbstractDevice<T extends AbstractDevice.Builder<T>> implements Device{

	private final Integer version;
	private final Optional<Integer> productCode;
	private final Optional<BigInteger> serialNumber;
	private final List<Connector> connectors;
	private static final Logger log = Logger.getLogger(AbstractDevice.class.getName());
	
	protected AbstractDevice(Builder<T> builder) {
		this.version = builder.getVersion();
		this.productCode = builder.getProductCode();
		this.serialNumber = builder.getSerialNumber();
		this.connectors = convertToConnectorList(builder.connectorTypes);
	}
	
	//converts the given list of connector Types into a list of connectors for the abstract device
	private List<Connector> convertToConnectorList(List<Connector.Type> connectorTypes) {
		if(connectorTypes == null) {
			return null;
		}
		//initialize an array list
		List<Connector> toReturn = new ArrayList<Connector>();
		//counter for index
		int index = 0;
		
		//for each connector type in the list of connector types
		for(Connector.Type t : connectorTypes) {
			//add a new connector into the array list
			toReturn.add(new Connector(this, index, t));
			index++;
		}
		
		return toReturn;
	}
	
	public Logger getLogger() {
		return log;
	}
	
	//validator for device.recv method
	public void validate(Message message, Connector connector) {
		if(isNull (message, connector)) {
			throw new NullPointerException("Argument inputs cannot be null.");
		}
		else if (!hasConnector(connector)) {
			throw new IllegalArgumentException("The connector must belong to the input device.");
		}
		else {
			//no other exception needs to be thrown
		}
	}	
	
	//checks if either the message or connector is null
	private boolean isNull(Message message, Connector connector) {
		return (message == null || connector == null);
	}
	
	//checks if the connector list has the given connector
	private boolean hasConnector(Connector connector) {
		for(Connector c : this.connectors) {
			if(c.equals(connector)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Optional<Integer> getProductCode() {
		return this.productCode;
	}

	@Override
	public Optional<BigInteger> getSerialNumber() {
		return this.serialNumber;
	}

	@Override
	public Integer getVersion() {
		return this.version;
	}
	
	@Override
	public DeviceClass getDeviceClass() {
		return DeviceClass.UNSPECIFIED;
	}

	@Override
	//create a list of connector types from the connectors field
	public List<Connector> getConnectors() {
		return this.connectors;
	}

	@Override
	public Connector getConnector(int index) {
		//impossible to get an index of negative number
		if(index < 0) {
			throw new NullPointerException("Index must be greater than 0.");
		}
		//impossible to get an index larger than the size of the list
		else if(index > connectors.size()) {
			throw new NullPointerException("Index entered is larger than the number of connectors.");
		}
		else {
			return connectors.get(index);
		}
	}

	@Override
	public Integer getConnectorCount() {
		return connectors.size();
	}
	
	public static abstract class Builder<T> {
		private Integer version;
		private Optional<Integer> productCode;
		private Optional<BigInteger> serialNumber;
		private List<Connector.Type> connectorTypes;
		
		public Builder(Integer version) {
			this.version = version;
			this.productCode = Optional.empty();
			this.serialNumber = Optional.empty();
			this.connectorTypes = new ArrayList<Connector.Type>(); 
		}
		
		public Integer getVersion() {
			return this.version;
		}
		
		public Optional<Integer> getProductCode() {
			return this.productCode;
		}
		
		public Optional<BigInteger> getSerialNumber() {
			return this.serialNumber;
		}
		
		public T productCode(Integer productCode) {
			//sets productCode to given value, or empty optional if null
		    this.productCode = Optional.ofNullable(productCode);
			return getThis();
		}
		
		public T serialNumber(BigInteger serialNumber) {
			//sets serial number to given value, or empty optional if null
			this.serialNumber = Optional.ofNullable(serialNumber);
			return getThis();
		}
		
		public T connectors(List<Connector.Type> connectorTypes) {
			//if parameter input is null, set connectors to an empty list
			if(connectorTypes == null) {
				this.connectorTypes = new ArrayList<Connector.Type>();
			}
			//otherwise copy the given value into the list
			else {
				this.connectorTypes = connectorTypes;
			}

			return getThis();
		}
		
		protected abstract T getThis();
		
		protected List<Connector.Type> getConnectors() {
			return this.connectorTypes;
		}
		
		protected void validate() {
			//if the version number is null, throw an exception
			if (this.version == null) {
				throw new NullPointerException("This device's version number is null.");
			}
			else {
				//if it's not null, don't throw an exception
				 //so there is no else case
			}
		}
	}
}
