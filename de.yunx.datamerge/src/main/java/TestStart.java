import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import de.yunx.datamerge.initialization.EntityClassifyer;
import de.yunx.datamerge.initialization.PatstatDistributionCreator;

public class TestStart {
	static Connection con = null;
		
	public static void main(String[] args) {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(
					"jdbc:mysql://192.168.53.179:3306/patstat413",
					"patstat", "#p8st8!");
			
			PatstatDistributionCreator pdc = new PatstatDistributionCreator(con);
			//HMMClassifyer hmmc = new HMMClassifyer(con);
			
			
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		
		
	}

}


/*


CREATE TABLE `TLS206_PERSON_DISTRIBUTION` (
`TOKEN` VARCHAR(32) NULL DEFAULT NULL,
`NUM` INT(11) NULL DEFAULT NULL,
FULLTEXT INDEX `token` (`token`)
)
COLLATE='utf8_general_ci'
ENGINE=MyISAM;
);

 */