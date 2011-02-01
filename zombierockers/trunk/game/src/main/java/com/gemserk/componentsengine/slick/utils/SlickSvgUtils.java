package com.gemserk.componentsengine.slick.utils;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.geom.Vector2f;

import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGElement;

public class SlickSvgUtils {

	public List<Vector2f> loadPoints(String file, String pathName) {
		try {
			ArrayList<Vector2f> points = new ArrayList<Vector2f>();
//			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			ClassLoader classLoader = SlickSvgUtils.class.getClassLoader();
			URI fileUri = classLoader.getResource(file).toURI();
			SVGDiagram diagram = SVGCache.getSVGUniverse().getDiagram(fileUri);
			SVGElement element = diagram.getElement(pathName);
			List vector = element.getPath(null);
			com.kitfox.svg.Path pathSVG = (com.kitfox.svg.Path) vector.get(1);
			Shape shape = pathSVG.getShape();
			PathIterator pathIterator = shape.getPathIterator(null, 0.001d);
			float[] coords = new float[2];

			while (!pathIterator.isDone()) {
				pathIterator.currentSegment(coords);
				points.add(new Vector2f(coords[0], coords[1]));
				pathIterator.next();
			}

			return points;
		} catch (URISyntaxException e) {
			throw new RuntimeException("failed to load svg " + pathName + " from " + file, e);
		}
	}

}