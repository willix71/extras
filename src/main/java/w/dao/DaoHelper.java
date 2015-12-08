package w.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for common SQL dao operations
 *
 * @author wkeyser
 *
 */
public class DaoHelper {

	private static final Logger logger = LoggerFactory.getLogger(DaoHelper.class);

	/**
	 * Object that when it's toString is called, returns one value and another
	 * one for every other call.
	 *
	 * @return
	 */
	public static Object criteriaLinker(final String first, final String next) {
		return new Object() {
			boolean isFirst = true;

			@Override
			public String toString() {
				if (this.isFirst) {
					this.isFirst = false;
					return first;
				} else {
					return next;
				}
			}
		};
	}

	/**
	 * Closes an SQL Connection
	 */
	public static void close(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				logger.error("Unable to close the connection", e);
			}
		}
	}

	/**
	 * Closes an SQL Statement
	 */
	public static void close(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				logger.error("Unable to close the statement", e);
			}
		}
	}

	/**
	 * Closes an SQL ResultSet
	 */
	public static void close(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (Exception e) {
				logger.error("Unable to close the result set", e);
			}
		}
	}

	public static void close(Connection conn, Statement stmt, ResultSet rs) {
		close(conn);
		close(stmt);
		close(rs);
	}

	/**
	 * Closes an SQL connections, statements and resultSets
	 */
	public static void closeAll(Object... objects) {
		// first close all resultSets
		for (Object o : objects) {
			if (o instanceof ResultSet) {
				close((ResultSet) o);
			}
		}
		// then the statements
		for (Object o : objects) {
			if (o instanceof Statement) {
				close((Statement) o);
			}
		}
		// finally the connections
		for (Object o : objects) {
			if (o instanceof Connection) {
				close((Connection) o);
			}
		}
	}

	/**
	 * Executes the lines of a script
	 */
	public void excuteScript(DataSource ds, Reader source) throws IOException, SQLException {
		logger.info("Executing script");
		
		try (BufferedReader reader = new BufferedReader(source);
				Statement stament = ds.getConnection().createStatement()) {
			String line, sql = "";
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("--"))
					continue;
				line = line.trim();
				if (line.isEmpty() || line.equals(";"))
					continue;
				sql += line;
				if (line.endsWith(";")) {
					stament.execute(sql);
					sql = "";
				}
			}
		}
	}
}
