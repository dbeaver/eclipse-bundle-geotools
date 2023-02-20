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
 */
package org.jkiss.geotools.oracle;

/**
 * SDOGeometryEType is a set of constants used to describe Oracle SDOUtils Geometries.
 *
 * @author Jody Garnett, Refractions Research, Inc.
 * @author $Author: jgarnett $ (last modification)
 * @version $Id$
 */
public interface SDOGeometryEType {
    /** <code>SDOGeometryEType</code> code representing custom geometries (like splines) */
    int CUSTOM = 0;

    /** <code>SDOGeometryEType</code> code representing Point */
    int POINT = 1;

    /** <code>SDOGeometryEType</code> code representing Line */
    int LINE = 2;

    /** <code>SDOGeometryEType</code> code representing Polygon (not recommended) */
    int POLYGON = 3;

    /** <code>SDOGeometryEType</code> code representing exterior CCW polygon ring */
    int POLYGON_EXTERIOR = 1003;

    /** <code>SDOGeometryEType</code> code representing interior CW polygon ring */
    int POLYGON_INTERIOR = 2003;

    /** <code>SDOGeometryEType</code> code representing exterior surface CCW polygon ring */
    int FACE_EXTERIOR = 1007;

    /** <code>SDOGeometryEType</code> code representing interior surface CCW polygon ring */
    int FACE_INTERIOR = 2006;

    /**
     * <code>SDOGeometryEType</code> code representing compound linestring
     *
     * <p>A compound polygon represents its edges using a combination of sequence of straight and
     * curved edges.
     *
     * <p>Compound LineString is not representatble as a JTS Geometry
     */
    int COMPOUND = 4;

    /**
     * <code>SDOGeometryEType</code> code representing compound polygon.
     *
     * <p>A compound polygon represents its edge using a combination of sequence of straight and
     * curved edges.
     *
     * <p>Compound Polygon is not representatble as a JTS Geometry
     */
    int COMPOUND_POLYGON = 5;

    /**
     * <code>SDOGeometryEType</code> code representing compound exterior CCW polygon ring
     *
     * <p>A compound polygon represents its edges using a combination of sequence of straight and
     * curved edges.
     *
     * <p>Compound Polygon Interior is not representatble as a JTS Geometry
     */
    int COMPOUND_POLYGON_EXTERIOR = 1005;

    /**
     * <code>SDOGeometryEType</code> code representing compound interior CW polygon ring
     *
     * <p>A compound polygon represents its edges using a combination of sequence of straight and
     * curved edges.
     *
     * <p>Compound Polygon Exterior is not representatble as a JTS Geometry
     */
    int COMPOUND_POLYGON_INTERIOR = 2005;
}
