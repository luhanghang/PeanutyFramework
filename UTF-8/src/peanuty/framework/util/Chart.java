/*
 * Created on 2005-5-11
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package peanuty.framework.util;

import java.util.*;

import org.jfree.data.general.*;
import org.jfree.chart.*;
import org.jfree.chart.servlet.*;
import org.jfree.data.category.*;
import org.jfree.ui.TextAnchor;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.labels.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.renderer.category.LineRenderer3D;
import org.jfree.data.*;

import org.jdom.*;

import peanuty.framework.base.*;

import javax.servlet.http.*;

/**
 * @author Lu Hang
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Chart {

	private final static String CONFIGFILE = "Chart.xml";

	private static List getChartObjectList() {
		Element root = XMLHandler.openXML(CONFIGFILE);
		return root.getChildren("Object");
	}

	public static String getSimplePieChart(Map dataSource, String objectName,
			HttpSession session) throws Throwable {
		DefaultPieDataset dataset = new DefaultPieDataset();

		Element chartObject = XMLHandler.getElementByAttribute(
				getChartObjectList(), "name", objectName);

		String title = chartObject.getAttributeValue("title");

		int width = Integer.parseInt(chartObject.getAttributeValue("width"));
		int height = Integer.parseInt(chartObject.getAttributeValue("height"));

		Element LabelKeys = chartObject.getChild("Labels");
		Element ValueKeys = chartObject.getChild("Values");

		String valueKey = ValueKeys.getText();
		String valueType = ValueKeys.getAttributeValue("type");
		List labelKeys = LabelKeys.getChildren("Label");
		String labelKey = LabelKeys.getText();

		if (valueType.equalsIgnoreCase("number")) {
			for (int i = 0; i < dataSource.size(); i++) {
				Map rec = (Map) dataSource.get("ROW" + i);
				Number value = (Number) rec.get(valueKey);
				String label;
				if (labelKeys.isEmpty()) {
					label = DataFilter.show(rec,labelKey);
				} else {
					label = ((Element) labelKeys.get(i)).getText();
				}
				dataset.setValue(label, value);
			}
		} else {
			for (int i = 0; i < dataSource.size(); i++) {
				Map rec = (Map) dataSource.get("ROW" + i);
				double value = (Double) rec.get(valueKey);
				String label;
				if (labelKeys.isEmpty()) {
					label = DataFilter.show(rec,labelKey);
				} else {
					label = ((Element) labelKeys.get(i)).getText();
				}
				dataset.setValue(label, value);
			}
		}

		JFreeChart chart = ChartFactory.createPieChart3D(
				title, dataset,
				chartObject.getAttribute("showLegend").getBooleanValue(),
				chartObject.getAttribute("showToolTips").getBooleanValue(),
				chartObject.getAttribute("urls").getBooleanValue());

		PiePlot3D pie3dplot = (PiePlot3D) chart.getPlot();

		float alpha = 0.7F;
		if(chartObject.getAttribute("alpha") != null){
			alpha = chartObject.getAttribute("alpha").getFloatValue();
		}
		pie3dplot.setForegroundAlpha(alpha);

		return ServletUtilities.saveChartAsPNG(chart, width, height, null,
				session);
	}

	public static String getSimpleBarChart(Map dataSource, String objectName,
			HttpSession session) throws Throwable {
		Element chartObject = XMLHandler.getElementByAttribute(
				getChartObjectList(), "name", objectName);

		List invokeFields = chartObject.getChild("InvokeFields").getChildren("Field");
		double[][] data = new double[invokeFields.size()][dataSource.size()];
		String[] rowKeys = new String[invokeFields.size()];
		String[] columnKeys = new String[dataSource.size()];
		String columnLabel = chartObject.getChildText("ColumnLabel");

		for (int i = 0; i < dataSource.size(); i++) {
			Map rec = (Map) dataSource.get("ROW" + i);
			columnKeys[i] = DataFilter.show(rec,columnLabel);
			for (int j = 0; j < invokeFields.size(); j++) {
				data[j][i] = Double.parseDouble(rec.get(((Element) invokeFields.get(j)).getAttributeValue("name")).toString());
			}
		}
		for (int i = 0; i < invokeFields.size(); i++) {
			rowKeys[i] = ((Element) invokeFields.get(i))
					.getAttributeValue("label");
		}

		CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
				rowKeys, columnKeys, data);

		PlotOrientation plotOrientation = chartObject.getAttributeValue(
				"plotOrientation").equalsIgnoreCase("VERTICAL") ? PlotOrientation.VERTICAL
				: PlotOrientation.HORIZONTAL;
		JFreeChart chart = ChartFactory.createBarChart3D(
				chartObject.getAttributeValue("title"),
				chartObject.getAttributeValue("categoryAxisLabel"),
				chartObject.getAttributeValue("valueAxisLabel"),
				dataset,
				plotOrientation,
				chartObject.getAttribute("showLegend").getBooleanValue(),
				chartObject.getAttribute("showToolTips").getBooleanValue(),
				chartObject.getAttribute("urls").getBooleanValue());

		CategoryPlot C3dplot = (CategoryPlot) chart.getPlot();
		if(chartObject.getAttribute("alpha") != null){
			C3dplot.setForegroundAlpha(chartObject.getAttribute("alpha").getFloatValue());
		}

		BarRenderer3D barrenderer = (BarRenderer3D)C3dplot.getRenderer();
        barrenderer.setLabelGenerator(new StandardCategoryLabelGenerator());
        barrenderer.setItemLabelsVisible(true);
        barrenderer.setPositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE1, TextAnchor.BASELINE_CENTER));

        int width, height;
		if(chartObject.getAttributeValue("width").equalsIgnoreCase("auto")){
			width = (50 * dataSource.size()) * invokeFields.size() + 100;
		} else {
			width = Integer.parseInt(chartObject.getAttributeValue("width"));
		}
		if(chartObject.getAttributeValue("height").equalsIgnoreCase("auto")){
			height = (50 * dataSource.size()) * invokeFields.size() + 100;
		} else {
			height = Integer.parseInt(chartObject.getAttributeValue("height"));
		}

		return ServletUtilities.saveChartAsPNG(chart, width, height, session);
	}

	 public static String getBarSeries(Map dataSource, String objectName, HttpSession session) throws Exception{
	        DefaultKeyedValues barValues = new DefaultKeyedValues();
	        DefaultKeyedValues seriesValues = new DefaultKeyedValues();
	        Element chartObject = XMLHandler.getElementByAttribute(
					getChartObjectList(), "name", objectName);

	        Element barField = chartObject.getChild("BarFields").getChild("Field");
	        Element seriesField = chartObject.getChild("SeriesFields").getChild("Field");

	        for(int i = 0; i < dataSource.size(); i++){
	        	Map rec = (Map)dataSource.get("ROW" + i);
	        	barValues.addValue(DataFilter.show(rec,chartObject.getChildText("ColumnLabel")), Double.parseDouble(rec.get(barField.getAttributeValue("name")).toString()));
	        	seriesValues.addValue(DataFilter.show(rec,chartObject.getChildText("ColumnLabel")), Double.parseDouble(rec.get(seriesField.getAttributeValue("name")).toString()));
	        }

	        CategoryDataset dataset = DatasetUtilities.createCategoryDataset(barField.getAttributeValue("label"), barValues);

	        PlotOrientation plotOrientation = chartObject.getAttributeValue(
			"plotOrientation").equalsIgnoreCase("VERTICAL") ? PlotOrientation.VERTICAL
			: PlotOrientation.HORIZONTAL;
	        JFreeChart chart = ChartFactory.createBarChart3D(
					chartObject.getAttributeValue("title"),
					chartObject.getAttributeValue("categoryAxisLabel"),
					chartObject.getAttributeValue("valueAxisLabel"),
					dataset,
					plotOrientation,
					chartObject.getAttribute("showLegend").getBooleanValue(),
					chartObject.getAttribute("showToolTips").getBooleanValue(),
					chartObject.getAttribute("urls").getBooleanValue());

	        CategoryPlot categoryplot = chart.getCategoryPlot();
	        LineRenderer3D lineRenderer = new LineRenderer3D();
	        CategoryDataset datasetSeries = DatasetUtilities.createCategoryDataset(seriesField.getAttributeValue("label"), seriesValues);

	        categoryplot.setDataset(1, datasetSeries);
	        categoryplot.setRangeAxis(1, new NumberAxis3D(seriesField.getAttributeValue("label")));
	        categoryplot.setRenderer(1, lineRenderer);
	        categoryplot.mapDatasetToRangeAxis(1, 1);

	        BarRenderer3D barrenderer = (BarRenderer3D)categoryplot.getRenderer();
	        barrenderer.setLabelGenerator(new StandardCategoryLabelGenerator());
	        barrenderer.setItemLabelsVisible(true);
	        barrenderer.setPositiveItemLabelPosition(
	                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE1, TextAnchor.BASELINE_CENTER));

//	        lineRenderer.setLabelGenerator(new StandardCategoryLabelGenerator());
//	        lineRenderer.setItemLabelsVisible(true);
//	        lineRenderer.setPositiveItemLabelPosition(
//	                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE10, TextAnchor.CENTER));

	        float alpha = 0.7F;
			if(chartObject.getAttribute("alpha") != null){
				alpha = chartObject.getAttribute("alpha").getFloatValue();
			}
			categoryplot.setForegroundAlpha(alpha);

			int width, height;
			if(chartObject.getAttributeValue("width").equalsIgnoreCase("auto")){
				width = (50 * dataSource.size()) + 100;
			} else {
				width = Integer.parseInt(chartObject.getAttributeValue("width"));
			}
			if(chartObject.getAttributeValue("height").equalsIgnoreCase("auto")){
				height = (50 * dataSource.size()) + 100;
			} else {
				height = Integer.parseInt(chartObject.getAttributeValue("height"));
			}

	        return ServletUtilities.saveChartAsPNG(chart, width, height, session);
	    }
}
