package com.ym.excel;

import com.alibaba.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 基于XSSF and SAX (Event API)
 * 读取excel的第一个Sheet的内容
 * @author yzl
 *
 */
public class ReadExcelUtils {
    private int headCount = 0;
    private List<List<String>> list = new ArrayList<List<String>>();
    private static final Log log = LogFactory.getLog(ReadExcelUtils.class);

    /**
     * 采用SAX进行解析
     * @param filename
     * @param headRowCount
     * @return
     * @throws OpenXML4JException
     * @throws IOException
     * @throws SAXException
     * @throws Exception
     */
    public List<List<String>> processSAXReadSheet(String filename, int headRowCount) throws IOException, OpenXML4JException, SAXException   {
        headCount = headRowCount;

        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader( pkg );
        SharedStringsTable sst = r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(sst);

        Iterator<InputStream> sheets = r.getSheetsData();
        InputStream sheet = sheets.next();
        InputSource sheetSource = new InputSource(sheet);
        parser.parse(sheetSource);
        sheet.close();
        return list;
    }

    private XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
        XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        ContentHandler handler = new SheetHandler(sst);
        parser.setContentHandler(handler);
        return parser;
    }

    /**
     * SAX 解析excel
     */
    private class SheetHandler extends DefaultHandler {
        private SharedStringsTable sst;
        private String lastContents;
        private boolean nextIsString;
        private boolean isNullCell;
        //读取行的索引
        private int rowIndex = 0;
        //是否重新开始了一行
        private boolean curRow = false;
        private List<String> rowContent;

        private SheetHandler(SharedStringsTable sst) {
            this.sst = sst;
        }

        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            //节点的类型
            //System.out.println("---------begin:" + name);
            if(name.equals("row")){
                rowIndex++;
            }
            //表头的行直接跳过
            if(rowIndex <= headCount) return;
            curRow = true;
            // c => cell
            if(name.equals("c")) {
                String cellType = attributes.getValue("t");
                if(null == cellType){
                    isNullCell = true;
                }else{
                    isNullCell = false;
                    if(cellType.equals("s")) {
                        nextIsString = true;
                    } else {
                        nextIsString = false;
                    }
                }
            }
            // Clear contents cache
            lastContents = "";
        }

        public void endElement(String uri, String localName, String name)
                throws SAXException {
            if(rowIndex <= headCount) return;
            if(nextIsString) {
                int idx = Integer.parseInt(lastContents);
                lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
                nextIsString = false;
            }
            if(name.equals("v")) {
                if(curRow){
                    //是新行则new一行的对象来保存一行的值
                    if(null==rowContent){
                        rowContent = new ArrayList<String>();
                    }
                    rowContent.add(lastContents);
                }
            }else if(name.equals("c") && isNullCell){
                if(curRow){
                    //是新行则new一行的对象来保存一行的值
                    if(null==rowContent){
                        rowContent = new ArrayList<String>();
                    }
                    rowContent.add(null);
                }
            }

            isNullCell = false;

            if("row".equals(name)){
                list.add(rowContent);
                curRow = false;
                rowContent = null;
            }
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            lastContents += new String(ch, start, length);
        }
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) throws Exception {
        ReadExcelUtils howto = new ReadExcelUtils();
        String fileName = "d:/test.xlsx";
        List<List<String>> list = howto.processSAXReadSheet(fileName, 1);
        System.out.println(JSON.toJSONString(list));
    }
}
