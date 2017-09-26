package com.ym.excel;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 抽象Excel2007读取器，excel2007的底层数据结构是xml文件，采用SAX的事件驱动的方法解析
 * xml，需要继承DefaultHandler，在遇到文件内容时，事件会触发，这种做法可以大大降低
 * 内存的耗费，特别使用于大数据量的文件。
 *
 */
public class Excel2007Reader2 extends DefaultHandler {

    private String lastContents;
    private List<String> dataList;
    private int rowNumber = 1;
    private List<String> currentCellData = Lists.newArrayList();
    private int totalSize = 0; //总行数


    public void importExcel2007(final InputStream is) {


        BufferedInputStream bis = null;
        try {
            long beginTime = System.currentTimeMillis();

            List<String> dataList = Lists.newArrayList();

            bis = new BufferedInputStream(is);
            OPCPackage pkg = OPCPackage.open(bis);
            XSSFReader r = new XSSFReader(pkg);

            XMLReader parser = XMLReaderFactory.createXMLReader();
            ContentHandler handler = new Excel2007Reader2();
            parser.setContentHandler(handler);

            Iterator<InputStream> sheets = r.getSheetsData();
            while (sheets.hasNext()) {
                InputStream sheet = null;
                try {
                    sheet = sheets.next();
                    InputSource sheetSource = new InputSource(sheet);
                    parser.parse(sheetSource);
                } catch (Exception e) {
                    throw e;
                } finally {
                    IOUtils.closeQuietly(sheet);
                }
            }

            long endTime = System.currentTimeMillis();
            Map<String, Object> context = Maps.newHashMap();
            context.put("seconds", (endTime - beginTime) / 1000);
        } catch (Exception e) {
            Map<String, Object> context = Maps.newHashMap();
            context.put("error", e.getMessage());
        } finally {
            IOUtils.closeQuietly(bis);
        }
    }

    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        if ("row".equals(name)) {//如果是行开始 清空cell数据 重来
            rowNumber = Integer.valueOf(attributes.getValue("r"));//当前行号
            if (rowNumber == 1) {
                return;
            }
            currentCellData.clear();
        }

        lastContents = "";
    }

    public void endElement(String uri, String localName, String name) throws SAXException {

        if ("row".equals(name)) {//如果是行开始 清空cell数据 重来
            if (rowNumber == 1) {
            return;
            }

            System.out.println(JSON.toJSONString(currentCellData.get(1)));

            totalSize++;

        }

        if ("c".equals(name)) {//按照列顺序添加数据
            currentCellData.add(lastContents);
        }


    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        String text = new String(ch, start, length);
        lastContents += text;
    }

    public static void main(String[] args) throws Exception {
        new Excel2007Reader2().importExcel2007(new FileInputStream("d://test.xlsx"));
    }
}
