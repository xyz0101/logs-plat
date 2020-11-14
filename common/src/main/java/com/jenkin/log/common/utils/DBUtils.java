package com.jenkin.log.common.utils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @Copyright: Shanghai jenkin Company.All rights reserved.
 * @Description:
 * @author: jenkin
 * @since: 2019/4/23 11:16
 * @history: 1.2019/4/23 created by jenkin
 */
public class DBUtils {
    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    public static Connection getDBConnect() {
            return getDBLocalConnect();
    }



    private static Connection getDBLocalConnect() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://mall.jenkin.tech:7307/log_platform?characterEncoding=utf8";
            String user = "root";
            String password = "Zhoujin@Zcr0807";
            System.out.println("开始获取连接");

            conn = DriverManager.getConnection(url, user, password);
            System.out.println("获取完成");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }
    public static void cancelAutoCommit(Connection conn) {
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void commitTransaction(Connection conn)  {
        try {
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行sql更新语句
     * @param updateSql
     * @param inParams
     * @return
     */
    public static int quickExeUpdateSQL( Connection conn, String updateSql, Object[] inParams) {

        PreparedStatement st = null;
        int num=0;
        try {
            if (inParams != null && inParams.length > 0) {
                st = conn.prepareStatement(updateSql);

                int i = 1;
                for (Object in : inParams) {
                    if (in == null) {
                        st.setObject(i, null);
                    } else {
                        if (in instanceof Integer) {
                            Integer ins = (Integer)in;
                            st.setInt(i, ins.intValue());
                        } else if (in instanceof Double) {
                            Double dou = (Double)in;
                            st.setDouble(i, dou.doubleValue());
                        } else if (in instanceof Long) {
                            Long lo = (Long)in;
                            st.setLong(i, lo.longValue());
                        } else if (in instanceof Date) {
                            Date d = (Date)in;
                            st.setDate(i, new Date(d.getTime()));
                        } else if (in instanceof String) {
                            st.setString(i, in.toString());
                        } else {
                            st.setObject(i, in);
                        }
                    }
                    i++;
                }
            } else {
                st = conn.prepareStatement(updateSql);
            }
            st.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closePst(st);
        }

        return num;
    }

    /**
     * 执行sql，返回结果集合
     * @param selectSql
     * @param inParams
     * @return
     */
    public static List<Object[]> quickExeSelectResult(Connection  conn, String selectSql, Object... inParams) {
        List<Object[]> res = new ArrayList<Object[]>();
        PreparedStatement st = null;
        ResultSet resSet = null;
        long b = System.currentTimeMillis();
        try {
            if (inParams != null && inParams.length > 0) {


                st = conn.prepareStatement(selectSql);
                //一次性向服务器获取两千条数据到结果集，默认是十条，当数据量大的时候需要频繁与服务器交互，耗时
                //也不能太大，会报OOM 此处为提升响应速度会增加内存开销
                //在最大堆内存512MB的情况下超过4000会导致内存溢出
                st.setFetchSize(6000);

                // Util.outPut("准备SQL耗时=====>"+(System.currentTimeMillis()-b));
                int i = 1;
                for (Object in : inParams) {
                    if (in == null) {
                        st.setObject(i, null);
                    } else {
                        if (in instanceof Integer) {
                            Integer ins = (Integer)in;
                            st.setInt(i, ins.intValue());
                        } else if (in instanceof Double) {
                            Double dou = (Double)in;
                            st.setDouble(i, dou.doubleValue());
                        } else if (in instanceof Long) {
                            Long lo = (Long)in;
                            st.setLong(i, lo.longValue());
                        } else if (in instanceof Date) {
                            Date d = (Date)in;
                            st.setDate(i, new Date(d.getTime()));
                        } else if (in instanceof String) {
                            st.setString(i, in.toString());
                        } else {
                            st.setObject(i, in);
                        }
                    }
                    i++;
                }
            } else {
                st = conn.prepareStatement(selectSql);
            }
            b = System.currentTimeMillis();
            resSet = st.executeQuery();

            //  Util.outPut("查询耗时=====>"+(System.currentTimeMillis()-b));
            ResultSetMetaData data = resSet.getMetaData();
            int columnCount = data.getColumnCount();
            b = System.currentTimeMillis();
            while (resSet.next()) {
                Object[] row = new Object[columnCount];
                for (int j = 1; j <= columnCount; j++) {
                    row[j - 1] = resSet.getObject(j);
                }
                res.add(row);
            }
            //  Util.outPut("组装数据耗时=====>"+(System.currentTimeMillis()-b)  + "     大小 "+resSet.getRow() );
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closePst(st);
            closeRs(resSet);
        }

        return res;
    }
    /**
     * 执行sql，返回结果集合(list<map>)
     * @param selectSql
     * @param inParams
     * @return
     */
    public static List<Map<String,Object>> quickExeSelectResultMap(Connection conn, String selectSql, Object[] inParams) {
        List<Map<String,Object>> res = new ArrayList<Map<String,Object>>();
        PreparedStatement st = null;
        ResultSet resSet = null;
        try {
            if (inParams != null && inParams.length > 0) {
                st = conn.prepareStatement(selectSql);
                int i = 1;
                for (Object in : inParams) {
                    if (in == null) {
                        st.setObject(i, null);
                    } else {
                        if (in instanceof Integer) {
                            Integer ins = (Integer)in;
                            st.setInt(i, ins.intValue());
                        } else if (in instanceof Double) {
                            Double dou = (Double)in;
                            st.setDouble(i, dou.doubleValue());
                        } else if (in instanceof Long) {
                            Long lo = (Long)in;
                            st.setLong(i, lo.longValue());
                        } else if (in instanceof Date) {
                            Date d = (Date)in;
                            st.setDate(i, new Date(d.getTime()));
                        } else if (in instanceof String) {
                            st.setString(i, in.toString());
                        } else {
                            st.setObject(i, in);
                        }
                    }
                    i++;
                }
            } else {
                st = conn.prepareStatement(selectSql);
            }

            resSet = st.executeQuery();

            ResultSetMetaData data = resSet.getMetaData();
            int columnCount = data.getColumnCount();

            while (resSet.next()) {
                // Object[] row = new Object[columnCount];
                Map<String,Object> rowMap = new HashMap<String,Object>();
                for (int j = 1; j <= columnCount; j++) {
                    rowMap.put(data.getColumnName(j).toLowerCase(), resSet.getObject(j));
                }
                res.add(rowMap);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closePst(st);
            closeRs(resSet);
        }

        return res;
    }
    /**
     *关闭预处理语句
     * @param pst
     */
    public static void closePst( PreparedStatement pst) {

        try {

            if (pst != null) {
                pst.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    /**
     *关闭call
     * @param
     */
    public static void closeCall( CallableStatement call) {

        try {

            if (call != null) {
                call.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    /**
     *关闭数据库连接
     * @param conn
     */
    public static void closeConn(Connection conn ) {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("释放连接");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    /**
     *关闭结果集
     * @param rs
     */
    public static void closeRs(ResultSet rs){
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 驼峰转下划线
     * @param str
     * @return
     */
    public static String humpToLine2(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static <T> T quickExeSelectResultForObject(Connection connection, String sql, Class<T> tClass, Object... objects) {
        List<Map<String, Object>> res = quickExeSelectResultMap(connection, sql, objects);
        T t=null;
        if(res!=null&&res.size()>0) {
            Map<String, Object> map = res.get(0);
            try {
                t = tClass.newInstance();
                for (Field declaredField : tClass.getDeclaredFields()) {
                    String key = humpToLine2(declaredField.getName());
                    Object o = map.get(key);
                    if(o!=null){
                        declaredField.setAccessible(true);
                        declaredField.set(t,o);
                    }
                }
                return t;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return t;
    }
}
