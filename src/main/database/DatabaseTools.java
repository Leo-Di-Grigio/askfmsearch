package main.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

final class DatabaseTools {
    
    static PreparedStatement getPreparedStatement(Connection connection, final String sql, final int count, final int collumns) throws SQLException{
        final StringBuilder builder = new StringBuilder(sql);
        String placeholders = getInsertPlaceholders(collumns);
        
        for ( int i = 0; i < count; i++ ) {
            if ( i != 0 ) {
                builder.append(",");
            }
            builder.append(placeholders);
        }
        
        return connection.prepareStatement(builder.toString());
    }
    
    static PreparedStatement getPreparedStatementOnDuplicate(Connection connection, final String sql, final int count, final int collumns, final String sqlOnDuplicate) throws SQLException{
        final StringBuilder builder = new StringBuilder(sql);
        String placeholders = getInsertPlaceholders(collumns);
        
        for ( int i = 0; i < count; i++ ) {
            if ( i != 0 ) {
                builder.append(",");
            }
            builder.append(placeholders);
        }
        builder.append(sqlOnDuplicate);
        
        return connection.prepareStatement(builder.toString());
    }
    
    private static String getInsertPlaceholders(int placeholderCount) {
        final StringBuilder builder = new StringBuilder("(");
        for ( int i = 0; i < placeholderCount; i++ ) {
            if ( i != 0 ) {
                builder.append(",");
            }
            builder.append("?");
        }
        return builder.append(")").toString();
    }
}