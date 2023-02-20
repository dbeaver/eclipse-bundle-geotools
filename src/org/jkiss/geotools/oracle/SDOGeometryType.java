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
 *
 *    Refractions Research Inc. Can be found on the web at:
 *    http://www.refractions.net/
 *
 *    Created on Oct 31, 2003
 */
package org.jkiss.geotools.oracle;

import org.locationtech.jts.geom.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bowens
 *     <p>To change the template for this generated type comment go to Window>Preferences>Java>Code
 *     Generation>Code and Comments
 */
public interface SDOGeometryType {
    /** <code>SDOGeometryType</code> code representing unknown geometies (like splines) */
    int UNKNOWN = 00;

    /** <code>SDOGeometryType</code> code representing Point */
    int POINT = 01;

    /** <code>SDOGeometryType</code> code representing Line (or Curve) */
    int LINE = 02;

    /** <code>SDOGeometryType</code> code representing Curve (or Line) */
    int CURVE = 02;

    /** <code>SDOGeometryType</code> code representing Polygon */
    int POLYGON = 03;

    /** <code>SDOGeometryType</code> code representing Collection */
    int COLLECTION = 04;

    /** <code>SDOGeometryType</code> code representing Multpoint */
    int MULTIPOINT = 05;

    /** <code>SDOGeometryType</code> code representing Multiline (or Multicurve) */
    int MULTILINE = 06;

    /** <code>SDOGeometryType</code> code representing Multicurve (or Multiline) */
    int MULTICURVE = 06;

    /** <code>SDOGeometryType</code> code representing MULTIPOLYGON */
    int MULTIPOLYGON = 07;

    /** <code>SDOGeometryType</code> code representing SOLID */
    int SOLID = 8;

    /** <code>SDOGeometryType</code> code representing SOLID */
    int MULTISOLID = 9;

    /**
     * A map from geomery type, as a string, to JTS Geometry. See Oracle Spatial documentation,
     * Table 2-1, Valid SDO_GTYPE values.
     */
    Map GEOM_CLASSES = Collections.unmodifiableMap(new GeomClasses());

    final class GeomClasses extends HashMap {
        private static final long serialVersionUID = -3359664692996608331L;

        public GeomClasses() {
            super();
            put("UNKNOWN", Geometry.class);
            put("POINT", Point.class);
            put("LINE", LineString.class);
            put("CURVE", LineString.class);
            put("POLYGON", Polygon.class);
            put("COLLECTION", GeometryCollection.class);
            put("MULTIPOINT", MultiPoint.class);
            put("MULTILINE", MultiLineString.class);
            put("MULTICURVE", MultiLineString.class);
            put("MULTIPOLYGON", MultiPolygon.class);
        }
    }
}
