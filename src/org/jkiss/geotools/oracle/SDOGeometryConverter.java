/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.jkiss.geotools.oracle;

import org.locationtech.jts.geom.*;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.Struct;

/**
 * Sample use of SDOUtils class for simple JTS Geometry.
 *
 * <p>If needed I can make a LRSGeometryConverter that allows JTS Geometries with additional
 * ordinates beyond xyz.
 *
 * @author jgarnett
 */
public class SDOGeometryConverter {
    private final GeometryFactory geometryFactory;

    public SDOGeometryConverter() {
        this(new GeometryFactory());
    }

    public SDOGeometryConverter(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    public static final String DATATYPE = "MDSYS.SDO_GEOMETRY";
    /**
     * Used to handle MDSYS.SDO_GEOMETRY.
     *
     * @return <code>MDSYS.SDO_GEOMETRY</code>
     */
    public String getDataTypeName() {
        return DATATYPE;
    }

    /**
     * Ensure that obj is a JTS Geometry (2D or 3D) with no LRS measures.
     *
     * <p>This Converter does not support SpatialCoordinates
     *
     * @param geom the Geometry to be converted
     * @return <code>true</code> if <code>obj</code> is a JTS Geometry
     */
    public boolean isCapable(Geometry geom) {
        if (geom == null) return true;
        if (geom instanceof Point
                || geom instanceof MultiPoint
                || geom instanceof LineString
                || geom instanceof MultiLineString
                || geom instanceof Polygon
                || geom instanceof MultiPolygon
                || geom instanceof GeometryCollection) {
            int d = SDOUtils.D(geom);
            int l = SDOUtils.L(geom);
            return l == 0 && (d == 2 || d == 3);
        }
        return false;
    }

    /**
     * Convert provided SDO_GEOMETRY to JTS Geometry.
     *
     * <p>Will return <code>null</code> as <code>null</code>.
     *
     * @param sdoGeometry datum STRUCT to be converted to a double[]
     * @return JTS <code>Geometry</code> representing the provided <code>datum</code>
     * @throws SQLException
     */
    public Geometry asGeometry(Struct sdoGeometry) throws Exception {
        // Note Returning null for null Datum
        if (sdoGeometry == null) return null;

        Object data[] = sdoGeometry.getAttributes();
        final int GTYPE = asInteger(data[0], 0);
        final int SRID = asInteger(data[1], SDOUtils.SRID_NULL);
        final double POINT[] = asDoubleArray((Struct) data[2], Double.NaN);
        final int ELEMINFO[] = asIntArray((Array) data[3], 0);
        final double ORDINATES[] = asDoubleArray((Array) data[4], Double.NaN);

        Geometry geometry = SDOUtils.create(geometryFactory, GTYPE, SRID, POINT, ELEMINFO, ORDINATES);
        geometry.setUserData(sdoGeometry);
        return geometry;
    }

    /**
     * Used to convert double[] to SDO_ODINATE_ARRAY.
     *
     * <p>Will return <code>null</code> as an empty <code>SDO_GEOMETRY</code>
     *
     * @param geom Map to be represented as a STRUCT
     * @return STRUCT representing provided Map
     * @see net.refractions.jspatial.Converter#toDataType(java.lang.Object)
     *
    public Struct toSDO(Geometry geom) throws SQLException {
        return toSDO(geom, geom.getSRID());
    }*/

    /**
     * Used to convert double[] to SDO_ODINATE_ARRAY.
     *
     * <p>Will return <code>null</code> as an empty <code>SDO_GEOMETRY</code>
     *
     * @param geom Map to be represented as a STRUCT
     * @return STRUCT representing provided Map
     * @see net.refractions.jspatial.Converter#toDataType(java.lang.Object)
     *
    public Struct toSDO(Geometry geom, int srid) throws SQLException {
        if (geom == null || geom.isEmpty()) return asEmptyDataType();

        int gtype = SDOUtils.gType(geom);
        NUMBER SDO_GTYPE = new NUMBER(gtype);

        NUMBER SDO_SRID = (srid == SDOUtils.SRID_NULL || srid == 0) ? null : new NUMBER(srid);

        double[] point = SDOUtils.point(geom);
        STRUCT SDO_POINT;

        ARRAY SDO_ELEM_INFO;
        ARRAY SDO_ORDINATES;

        if (point == null) {
            final Envelope env = geom.getEnvelopeInternal();
            if (env.getWidth() > 0
                    && env.getHeight() > 0
                    && !(geom instanceof GeometryCollection)
                    && geom.isRectangle()) {
                // rectangle optimization. Actually, more than an optimization. A few operators
                // do not work properly if they don't get rectangular geoms encoded as rectangles
                // SDO_FILTER is an example of this silly situation
                SDO_POINT = null;
                int elemInfo[] = new int[] {1, 1003, 3};
                double ordinates[];
                if (SDOUtils.D(geom) == 2)
                    ordinates =
                            new double[] {
                                env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY()
                            };
                else
                    ordinates =
                            new double[] {
                                env.getMinX(), env.getMinY(), 0, env.getMaxX(), env.getMaxY(), 0
                            };

                SDO_POINT = null;
                SDO_ELEM_INFO = toARRAY(elemInfo, "MDSYS.SDO_ELEM_INFO_ARRAY");
                SDO_ORDINATES = toARRAY(ordinates, "MDSYS.SDO_ORDINATE_ARRAY");
            } else {
                int elemInfo[] = SDOUtils.elemInfo(geom);
                double ordinates[] = SDOUtils.ordinates(geom);

                SDO_POINT = null;
                SDO_ELEM_INFO = toARRAY(elemInfo, "MDSYS.SDO_ELEM_INFO_ARRAY");
                SDO_ORDINATES = toARRAY(ordinates, "MDSYS.SDO_ORDINATE_ARRAY");
            }
        } else { // Point Optimization
            Datum data[] =
                    new Datum[] {
                        toNUMBER(point[0]), toNUMBER(point[1]), toNUMBER(point[2]),
                    };
            SDO_POINT = toSTRUCT(data, "MDSYS.SDO_POINT_TYPE");
            SDO_ELEM_INFO = null;
            SDO_ORDINATES = null;
        }
        Datum attributes[] =
                new Datum[] {SDO_GTYPE, SDO_SRID, SDO_POINT, SDO_ELEM_INFO, SDO_ORDINATES};
        return toSTRUCT(attributes, DATATYPE);
    }
*/
    /**
     * Representation of <code>null</code> as an Empty <code>SDO_GEOMETRY</code>.
     *
     * @return <code>null</code> as a SDO_GEOMETRY
     *
    protected STRUCT asEmptyDataType() throws SQLException {
        return toSTRUCT(null, DATATYPE);
    }*/

/*
    protected final STRUCT toSTRUCT(Datum attributes[], String dataType) throws SQLException {
        if (dataType.startsWith("*.")) {
            dataType = "DRA." + dataType.substring(2); // TODO here
        }
        StructDescriptor descriptor = StructDescriptor.createDescriptor(dataType, connection);

        return new STRUCT(descriptor, connection, attributes);
    }
*/

    /**
     * Convience method for ARRAY construction.
     *
     * <p>Compare and contrast with toORDINATE - which treats <code>Double.NaN</code> as<code>NULL
     * </code>
     */
/*
    protected final ARRAY toARRAY(double doubles[], String dataType) throws SQLException {
        ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor(dataType, connection);

        return new ARRAY(descriptor, connection, doubles);
    }
*/

    /**
     * Convience method for ARRAY construction.
     *
     * <p>Forced to burn memory here - only way to actually place <code>NULL</code> numbers in the
     * ordinate stream.
     *
     * <ul>
     *   <li>JTS: records lack of data as <code>Double.NaN</code>
     *   <li>SDOUtils: records lack of data as <code>NULL</code>
     * </ul>
     *
     * <p>The alternative is to construct the array from a array of doubles, which does not record
     * <code>NULL</code> NUMBERs.
     *
     * <p>The results is an "MDSYS.SDO_ORDINATE_ARRAY" <code><pre>
     * list     = c1(1,2,0), c2(3,4,Double.NaN)
     * measures = {{5,6},{7,8}
     *
     * toORDINATE( list, measures, 2 )
     * = (1,2,5,7, 3,4,6,8)
     *
     * toORDINATE( list, measures, 3 )
     * = (1,2,0,5,7, 3,4,NULL,6,8)
     *
     * toORDINATE( list, null, 2 )
     * = (1,2, 3,4)
     * </pre></code>
     *
     * @param list CoordinateList to be represented
     * @param measures Per Coordinate Measures, <code>null</code> if not required
     * @param D Dimension of SDOCoordinates (limited to 2d, 3d)
     *
    protected final ARRAY toORDINATE(CoordinateList list, double measures[][], final int D)
            throws SQLException {
        ArrayDescriptor descriptor =
                ArrayDescriptor.createDescriptor("MDSYS.SDO_ORDINATE_ARRAY", connection);

        final int LENGTH = measures != null ? measures.length : 0;
        final int LEN = D + LENGTH;
        Datum data[] = new Datum[list.size() * LEN];
        int offset = 0;
        int index = 0;
        Coordinate coord;

        for (Iterator i = list.iterator(); i.hasNext(); index++) {
            coord = (Coordinate) i.next();

            data[offset++] = toNUMBER(coord.x);
            data[offset++] = toNUMBER(coord.y);
            if (D == 3) {
                data[offset++] = toNUMBER(coord.x);
            }
            for (int j = 0; j < LENGTH; j++) {
                data[offset++] = toNUMBER(measures[j][index]);
            }
        }
        return new ARRAY(descriptor, connection, data);
    }
*/
/*

    protected final ARRAY toORDINATE(double ords[]) throws SQLException {
        ArrayDescriptor descriptor =
                ArrayDescriptor.createDescriptor("MDSYS.SDO_ORDINATE_ARRAY", connection);

        final int LENGTH = ords.length;

        Datum data[] = new Datum[LENGTH];

        for (int i = 0; i < LENGTH; i++) {
            data[i] = toNUMBER(ords[i]);
        }
        return new ARRAY(descriptor, connection, data);
    }

    protected final ARRAY toATTRIBUTE(double ords[], String desc) throws SQLException {
        ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor(desc, connection);

        final int LENGTH = ords.length;

        Datum data[] = new Datum[LENGTH];

        for (int i = 0; i < LENGTH; i++) {
            data[i] = toNUMBER(ords[i]);
        }
        return new ARRAY(descriptor, connection, data);
    }

    /**
     * Convience method for NUMBER construction.
     *
     * <p>Double.NaN is represented as <code>NULL</code> to agree with JTS use.
     *
    protected final NUMBER toNUMBER(double number) throws SQLException {
        if (Double.isNaN(number)) {
            return null;
        }
        return new NUMBER(number);
    }

    protected final ARRAY toARRAY(int ints[], String dataType) throws SQLException {
        ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor(dataType, connection);

        return new ARRAY(descriptor, connection, ints);
    }


    protected final NUMBER toNUMBER(int number) {
        return new NUMBER(number);
    }

    protected final CHAR toCHAR(String s) {

        // make sure if the string is larger than one character, only take the first character
        if (s.length() > 1) s = new String((new Character(s.charAt(0))).toString());
        try {
            // BUG: make sure I am correct
            return new CHAR(s, CharacterSet.make(CharacterSet.ISO_LATIN_1_CHARSET));
        } catch (SQLException e) {
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.INFO, "", e);
        }
        return null;
    }
*/

    //
    // These functions present Datum as a Java type
    //
    /** Presents datum as an int */
    protected int asInteger(Object datum, final int DEFAULT) throws Exception {
        if (datum == null) return DEFAULT;
        return (Integer)datum.getClass().getMethod("intValue").invoke(datum);
    }
    /** Presents datum as a double */
    protected double asDouble(Object datum, final double DEFAULT) throws Exception {
        if (datum == null) return DEFAULT;
        return (Double)datum.getClass().getMethod("doubleValue").invoke(datum);
    }

    /** Presents struct as a double[] */
    protected double[] asDoubleArray(Struct struct, final double DEFAULT) throws Exception {
        if (struct == null) return null;
        return asDoubleArray(struct.getAttributes(), DEFAULT);
    }

    /** Presents array as a double[] */
    protected double[] asDoubleArray(Array array, final double DEFAULT) throws Exception {
        if (array == null) return null;

        return asDoubleArray(array.getArray(), DEFAULT);
    }

    protected double[] asDoubleArray(Object data, final double DEFAULT) throws Exception {
        if (data == null) return null;
        int length = java.lang.reflect.Array.getLength(data);
        double array[] = new double[length];
        for (int i = 0; i < length; i++) {
            array[i] = asDouble(java.lang.reflect.Array.get(data, i), DEFAULT);
        }
        return array;
    }

    protected double[] asDoubleArray(Object data[], final double DEFAULT) throws Exception {
        if (data == null) return null;
        double array[] = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            array[i] = asDouble(data[i], DEFAULT);
        }
        return array;
    }

    protected int[] asIntArray(Array array, int DEFAULT) throws Exception {
        if (array == null) return null;

        return asIntArray(array.getArray(), DEFAULT);
    }

    protected int[] asIntArray(Object data, final int DEFAULT) throws Exception {
        if (data == null) return null;
        int length = java.lang.reflect.Array.getLength(data);
        int array[] = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = asInteger(java.lang.reflect.Array.get(data, i), DEFAULT);
        }
        return array;
    }

    protected int[] asIntArray(Object data[], final int DEFAULT) throws Exception {
        if (data == null) return null;
        int array[] = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            array[i] = asInteger(data[i], DEFAULT);
        }
        return array;
    }
}
