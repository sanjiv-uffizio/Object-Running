	package projects.trakzee.testCases.web.multiobjectRunning;
	
	import java.io.*;
	import java.util.HashMap;
	import java.util.Map;
	
	public class StoreValues implements Serializable {
		private static final long serialVersionUID = 1L;
		private static final String FILE_PATH = System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator+"config.ser";
	
		private Map<String, String> data = new HashMap<>();
	
		public void set(String key, String value) {
			data.put(key, value);
			save();
		}
	
		public String get(String key) {
			return data.get(key);
		}
	
		public static StoreValues load() {
			File file = new File(FILE_PATH);
			if (!file.exists())
				return new StoreValues();
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
				return (StoreValues) ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
				return new StoreValues();
			}
		}
	
		private void save() {
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
				oos.writeObject(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void printAll() {
		    System.out.println("🔎 Stored Variables:");
		    data.forEach((k, v) -> System.out.println(k + " = " + v));
		}
	}
