package com.ym.mobile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.bcel.generic.ARETURN;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.formula.functions.Value;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Test;
import org.springframework.scheduling.annotation.Async;

import java.io.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author yangmeng44
 * @Date 2017/5/25
 */
public class MobileExport {

    static AtomicInteger rowIndex = new AtomicInteger(0);
    static AtomicLong maxId = new AtomicLong(Long.MAX_VALUE);

    /**
     * 导出文件的最大大小 超过这个大小会压缩
     */
    private static final int MAX_EXPORT_FILE_SIZE = 10 * 1024 * 1024; //10MB

    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static SSLConnectionSocketFactory sslsf = null;
    private static PoolingHttpClientConnectionManager cm = null;
    private static SSLContextBuilder builder = null;
    static {
        try {
            builder = new SSLContextBuilder();
            // 全部信任 不做身份鉴定
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            });
            sslsf = new SSLConnectionSocketFactory(builder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register(HTTP, new PlainConnectionSocketFactory())
                    .register(HTTPS, sslsf)
                    .build();
            cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(200);//max connection
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CloseableHttpClient getHttpClient() throws Exception {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setConnectionManager(cm)
                .setConnectionManagerShared(true)
                .build();
        return httpClient;
    }
    public static String readHttpResponse(HttpResponse httpResponse)
            throws ParseException, IOException {
        StringBuilder builder = new StringBuilder();
        // 获取响应消息实体
        HttpEntity entity = httpResponse.getEntity();
        // 响应状态
        builder.append("status:" + httpResponse.getStatusLine());
        builder.append("headers:");
        HeaderIterator iterator = httpResponse.headerIterator();
        while (iterator.hasNext()) {
            builder.append("\t" + iterator.next());
        }
        // 判断响应实体是否为空
        if (entity != null) {
            String responseString = EntityUtils.toString(entity);
            builder.append("response length:" + responseString.length());
            builder.append("response content:" + responseString.replace("\r\n", ""));
        }
        return builder.toString();
    }



    public static void main(String[] args) throws Exception {
        int rowAccessWindowSize = 10000; //内存中保留的行数，超出后会写到磁盘
        int perSheetRows = 100000; //每个sheet 10w条
        CloseableHttpClient httpClient = getHttpClient();
        String fileName = "d:/中移动订单21.xlsx";
        File file = new File(fileName);
        BufferedOutputStream out = null;
        SXSSFWorkbook wb = null;
        Sheet sheet = null;
        long beginTime = System.currentTimeMillis();

        try {
            wb = new SXSSFWorkbook(rowAccessWindowSize);
            wb.setCompressTempFiles(true);//生成的临时文件将进行gzip压缩

            String url = "select * from biz_purchase_order where id < %d order by id desc";
//            String url = "select * from biz_purchase_order where px_order_id in (170405000543191005,170502000620241005,170503000625261005,170503000626531005,170503000627431005,170504000631861005,170505000635741005,170505000638371005,170505000638801005,170508000640521005,170508000641011005,170508000641961005,170508000642321005,170508000642841005,170508000643491005,170508000643571005,170508000644321005,170508000644411005,170509000646041005,170509000646551005,170509000647131005,170509000648731005,170509000649131005,170509000649171005,170509000649181005,170509000650631005,170510000651691005,170510000652421005,170510000653161005,170510000653741005,170510000654391005,170510000654451005,170511000657851005,170511000659161005,170511000659961005,170512000662261005,170512000662311005,170512000662481005,170512000665961005,170512000666181005,170512000666401005,170512000666411005,170515000667941005,170516000671601005,170516000673921005,170516000673971005,170516000674831005,170517000677071005,170517000677261005,170518000683941005,170518000686781005,170519000689591005,170519000689851005,170522000693071005,170522000694751005,170522000694761005,170522000695021005,170522000695781005,170523000696841005,170523000697171005,170523000698981005,170523000702841005,170524000703111005,170524000705661005,170524000706701005,170524000707451005,170525000709851005,170525000710551005,170525000710631005,170525000710741005,170525000710841005,170525000711081005,170525000712651005,170525000713351005,170525000713381005,170525000713881005,170525000714101005,170525000714381005,170526000715701005,170526000716521005,170526000716631005,170526000717451005,170526000719021005,170526000719141005,170526000719581005,170526000719671005,170526000719981005,170527000720721005,170527000724001005,170527000724131005,170531000728931005,170531000729691005,170531000729841005,170531000730181005,170531000730201005,170531000730941005,170531000730951005,170531000731111005,170531000731211005,170531000731261005,170601000732341005,170601000732431005,170601000732921005,170601000734081005,170601000735261005,170601000735381005,170601000735901005,170601000737161005,170602000738201005,170602000739001005,170602000739791005,170602000740611005,170602000740771005,170602000741301005,170602000741861005,170602000742731005,170605000743721005,170605000744431005,170605000746041005,170605000746091005,170605000746151005,170605000746191005,170605000746211005,170605000747751005,170605000748571005,170606000749751005,170606000752281005,170606000752361005,170606000753631005,170606000754981005,170607000756381005,170607000756701005,170607000757611005,170607000757641005,170607000757691005,170607000758691005,170607000759231005,170607000759401005,170607000759801005,170607000760141005,170607000760191005,170607000760371005,170607000760531005,170607000760591005,170608000761921005,170608000762161005,170608000763351005,170608000763391005,170608000763871005,170608000763951005,170608000764431005,170608000764741005,170608000764851005,170608000764931005,170608000765471005,170608000765581005,170608000765991005,170608000768051005,170608000768701005,170608000768731005,170609000769171005,170609000770751005,170609000770981005,170609000771811005,170609000772041005,170609000773291005,170609000774621005,170609000774691005,170612000776231005,170612000776711005,170612000777631005,170612000778251005,170612000778431005,170612000778601005,170612000778881005,170612000779161005,170612000780021005,170613000782301005,170613000783081005,170613000785641005,170613000785771005,170613000785871005,170613000786811005,170613000786851005,170614000788771005,170614000789441005,170614000789461005,170614000789601005,170614000789761005,170614000791391005,170615000794031005,170615000794171005,170615000794991005,170615000795411005,170615000796321005,170615000797341005,170615000797821005,170615000797861005,170615000798431005,170616000799111005,170616000800961005,170616000801261005,170616000802251005,170616000804521005,170619000805981005,170619000806391005,170619000806491005,170619000809391005,170620000811291005,170620000811391005,170620000812661005,170620000813671005,170620000813691005,170620000814181005,170620000814451005,170620000814581005,170620000816911005,170621000817511005,170621000820251005,170621000820331005,170621000821701005,170621000822321005,170621000822391005,170621000822571005,170621000823111005,170622000824511005,170622000824841005,170622000825171005,170622000826971005,170622000827131005,170622000827871005,170622000829101005,170623000830391005,170623000831721005,170623000832511005,170623000833751005,170625000835061005,170625000835071005,170626000835381005,170626000835761005,170626000836061005,170626000836771005,170626000837751005,170626000837881005,170626000837901005,170626000838801005,170626000839941005,170627000841031005,170627000842951005,170627000844001005,170627000844431005,170627000844971005,170627000845001005,170627000845911005,170628000847761005,170628000848111005,170628000849821005,170628000851331005,170628000851431005,170629000854961005,170629000855411005,170629000855461005,170629000855581005,170629000855931005,170629000856541005,170629000857061005,170629000857081005,170630000859841005,170630000860001005,170630000860841005,170630000861031005,170630000861131005,170630000861631005,170630000861661005,170630000861911005)  and id < %d  order by id desc";
            sheet = wb.createSheet();
            int cellIndex = 0;
            Row row = sheet.createRow(rowIndex.getAndIncrement());
            for (Map.Entry<String, String> entry : getOrderMapper().entrySet()) {
                Cell cell = row.createCell(cellIndex++);
                cell.setCellValue(entry.getValue());
            }
            int former = rowIndex.get();
            while (rowIndex.get() == 1 || rowIndex.get() - former >= 100) {
                former = rowIndex.get();
                export(getOrderMapper(), httpClient,  sheet, url);
                System.out.println("maxId = " + maxId.get());
            }

            url = "select * from biz_purchase_order_entity where id < %d order by id desc";
            sheet = wb.createSheet();
            cellIndex = 0;
            rowIndex.set(0);
            maxId.set(Long.MAX_VALUE);
            row = sheet.createRow(rowIndex.getAndIncrement());
            for (Map.Entry<String, String> entry : getEntityMapper().entrySet()) {
                Cell cell = row.createCell(cellIndex++);
                cell.setCellValue(entry.getValue());
            }
            former = rowIndex.get();
            while (rowIndex.get() == 1 || rowIndex.get() - former >= 100) {
                former = rowIndex.get();
                export(getEntityMapper(), httpClient, sheet, url);
                System.out.println("maxId = " + maxId.get());
            }

            url = "select * from biz_purchase_order_err_info where id < %d order by id desc";
            sheet = wb.createSheet();
            cellIndex = 0;
            rowIndex.set(0);
            maxId.set(Long.MAX_VALUE);
            row = sheet.createRow(rowIndex.getAndIncrement());
            for (Map.Entry<String, String> entry : getErrInfoMapper().entrySet()) {
                Cell cell = row.createCell(cellIndex++);
                cell.setCellValue(entry.getValue());
            }
            former = rowIndex.get();
            while (rowIndex.get() == 1 || rowIndex.get() - former >= 100) {
                former = rowIndex.get();
                export(getErrInfoMapper(), httpClient, sheet, url);
                System.out.println("maxId = " + maxId.get());
            }

            out = new BufferedOutputStream(new FileOutputStream(file));
            wb.write(out);

            IOUtils.closeQuietly(out);

            if (needCompress(file)) {
                fileName = compressAndDeleteOriginal(fileName);
            }
        } catch (Exception e) {
            IOUtils.closeQuietly(out);
        } finally {
            // 清除本工作簿备份在磁盘上的临时文件
            wb.dispose();
        }
    }

    private static void export(Map<String, String> mapper, HttpClient httpClient, Sheet sheet, String url) throws Exception {
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("id", "13934"));
        nvps.add(new BasicNameValuePair("sql", String.format(url, maxId.get())));
        String cookie = "_AIRLINE_VALUE_=\"yc+6oyzJ7tvaLDIwMTctMDUtMTcsMSxPVw==\"; __utmz=122270672.1497415704.1.1.utmcsr=trade.jd.com|utmccn=(referral)|utmcmd=referral|utmcct=/shopping/order/getOrderInfo.action; user-key=36df6e7a-c94f-46c1-b1da-4f23a3a24a15; areaId=22; ipLocation=%u56db%u5ddd; cn=1; ipLoc-djd=22-1930-50947-52199.138065511; __jdv=50436146|direct|-|none|-|1499750864564; sso.jd.com=8c4e79f8173b4ec9bfe8091eb0ecd156; _jrda=1; wlfstk_smdl=b4pbi13t77zgpjrp562nn6do8veng8bs; TrackID=19GgWmLByLoooKrPIkR7e6FaSaKPvjxNR-qFPoUBs2oHDPExYleo7MwFn2hKRk3xn-vC1IQmk3smC6kYmugYmpcFgXSqQuWRMSVSUWFWhObU; pinId=TLxs1SqPvIdhDna5ukr88Q; pin=yangmengdx3; unick=yangmengdx3; _tp=UhB8RvsebkKzbUkJE3h1Zw%3D%3D; _pst=yangmengdx3; ceshi3.com=201; thor=F20D365E2EBFEC99D9DE55F060BD5ABCE153A45026EBDF9685338B75133C3EBC641185B0B46B98882D895B51CFD7646EB96C6769C3CF7B8F1DD7E3F07048A359782B1F3797F0E3C508E57CECBE3EB18FF6E1A1C9D9B591ED7A155B7598A683C2A0BA743D53BF2A226A0285811301B733339EFD3FA9C621CDFC95E2B57FCC64D9B4236A4D779BF475144FE71A3308220D; mt_xid=V2_52007VwtFUFhdB1MfG0wLBmRRElZVDFdbHUFOCFBnABJbVFoFak9KS1pWYAVHTl4IVAgDTBgPA3sLG1ReRAZeShkZXgQzBBICCWhRWx9PBV0DewMRQ11aWVoXQhhYNWUzEmJdW1NZHEEdWwdXAxdUWg%3D%3D; 3AB9D23F7A4B3C9B=PUSL24ZPED5LSN2OU6XIGV7YXQ4XDTWWYLGR5XQ44IKAME7BQ4CNZKHYU45EJ2YVCEYP7VQ3VNYHA6KWC7BINON4MQ; PHPSESSID=e2qiqdhvtajhssn58ko4dspm27; __jda=50436146.9f344d81cdf22c159e4039fed121883f.1485071150.1499836715.1499840678.497; __jdb=50436146.1.9f344d81cdf22c159e4039fed121883f|497.1499840678; __jdc=50436146; __jdu=9f344d81cdf22c159e4039fed121883f";
        HttpPost httpPost = new HttpPost("http://dbquery.jd.com/home/ajaxQueryData");
        httpPost.addHeader("Cookie", cookie);
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nvps, Consts.UTF_8);
        httpPost.setEntity(urlEncodedFormEntity);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            HttpEntity resEntity = httpResponse.getEntity();
            String json = EntityUtils.toString(resEntity);
            exportExcel2007(sheet,mapper, json);
        }
    }


    /**
     * 支持大数据量导出
     * excel 2007 每个sheet最多1048576行
     * @param user
     * @param contextRootPath
     * @param searchable
     */
    public static void exportExcel2007(Sheet sheet, Map<String, String> mapper, String json) {
        int cellIndex = 0;
        Row row = null;
        JSONArray jsonArray = JSONObject.parseObject(json).getJSONArray("rows");
        for (int i = 0; i < jsonArray.size(); i++) {
            cellIndex = 0;
            row = sheet.createRow(rowIndex.getAndIncrement());
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            for (Map.Entry<String, String> entry : mapper.entrySet()) {
                String code = entry.getKey();
                String value = jsonObject.getString(code);
                Cell cell = row.createCell(cellIndex++);
                cell.setCellValue(value);
                if ("id".equals(code)) {
                    maxId.set(Long.valueOf(value));
                }
            }
        }
    }
    private static boolean needCompress(final File file) {
        return file.length() > MAX_EXPORT_FILE_SIZE;
    }

    private static String compressAndDeleteOriginal(final String filename) {
        String newFileName = FilenameUtils.removeExtension(filename) + ".zip";
        compressAndDeleteOriginal(newFileName, filename);
        return newFileName;
    }

    private static void compressAndDeleteOriginal(final String newFileName, final String... needCompressFilenames) {
        zip(newFileName, needCompressFilenames);
        for(String needCompressFilename : needCompressFilenames) {
            FileUtils.deleteQuietly(new File(needCompressFilename));
        }
    }

    public static final void zip(String compressPath, String[] needCompressPaths) {
        File compressFile = new File(compressPath);

        List<File> files = Lists.newArrayList();
        for (String needCompressPath : needCompressPaths) {
            File needCompressFile = new File(needCompressPath);
            if (!needCompressFile.exists()) {
                continue;
            }
            files.add(needCompressFile);
        }
        try {
            ZipArchiveOutputStream zaos = null;
            try {
                zaos = new ZipArchiveOutputStream(compressFile);
                zaos.setUseZip64(org.apache.commons.compress.archivers.zip.Zip64Mode.AsNeeded);
                zaos.setEncoding("GBK");

                for (File file : files) {
                    addFilesToCompression(zaos, file, "");
                }

            } catch (IOException e) {
                throw e;
            } finally {
                org.apache.commons.io.IOUtils.closeQuietly(zaos);
            }
        } catch (Exception e) {
            FileUtils.deleteQuietly(compressFile);
            throw new RuntimeException("压缩失败", e);
        }
    }

    private static void addFilesToCompression(ZipArchiveOutputStream zaos, File file, String dir) throws IOException {

        ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file, dir + file.getName());
        zaos.putArchiveEntry(zipArchiveEntry);

        if (file.isFile()) {
            BufferedInputStream bis = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(file));
                org.apache.commons.io.IOUtils.copy(bis, zaos);
                zaos.closeArchiveEntry();
            } catch (IOException e) {
                throw e;
            } finally {
                org.apache.commons.io.IOUtils.closeQuietly(bis);
            }
        } else if (file.isDirectory()) {
            zaos.closeArchiveEntry();

            for (File childFile : file.listFiles()) {
                addFilesToCompression(zaos, childFile, dir + file.getName() + File.separator);
            }
        }
    }

    private static Map<String, String> getOrderMapper() {
        Map<String, String> mapper = Maps.newLinkedHashMap();
        mapper.put("id","主键");
        mapper.put("system_id","系统标识");
        mapper.put("system_name","系统名称");
        mapper.put("sup_code","供应渠道编码");
        mapper.put("token_str","验证标识");
        mapper.put("px_order_id","采购平台订单号");
        mapper.put("delivery_name","收货人姓名");
        mapper.put("delivery_company","收货人公司");
        mapper.put("delivery_province","收货省份");
        mapper.put("delivery_province_name","收货省份");
        mapper.put("delivery_city","收货");
        mapper.put("delivery_city_name","收货城市");
        mapper.put("delivery_county","收货区县");
        mapper.put("delivery_county_name","收货区县");
        mapper.put("delivery_town","收货乡镇");
        mapper.put("delivery_town_name","收货乡镇");
        mapper.put("delivery_address","收货街道地址");
        mapper.put("delivery_zip","邮政编码");
        mapper.put("delivery_phone","收货人座机");
        mapper.put("delivery_mobile","收货人手机");
        mapper.put("delivery_email","收货人邮箱");
        mapper.put("delivery_remark","收货人备注");
        mapper.put("creator_company","订货人公司");
        mapper.put("creator_name","订货人姓名");
        mapper.put("creator_phone","订货人座机");
        mapper.put("creator_mobile","订货人手机");
        mapper.put("invoice_type","发票类型");
        mapper.put("invoice_title","发票抬头");
        mapper.put("invoice_content","发票内容");
        mapper.put("receiver_name","收票人姓名");
        mapper.put("receiver_mobile","收票人手机号");
        mapper.put("customer_name","客户名称");
        mapper.put("created_time","下单时间");
        mapper.put("is_emergency","是否紧急订单");
        mapper.put("batch_id","紧急单批次");
        mapper.put("pay_way","支付方式");
        mapper.put("is_pre_order","是否预占单");
        mapper.put("sign_data","签名数据");
        mapper.put("created","创建时间");
        mapper.put("modified","更新时间");
        mapper.put("status","是否有效，1:有效,0:无效");
        mapper.put("version","版本号，更新自增1");
        mapper.put("remark","备注信息");
        mapper.put("po_state","订单状态0:新订单1:保存成功2:保存失败");
        mapper.put("submit_order_state","下单状态0:新订单1:下单成功2:下单失败");
        mapper.put("cancel_state","取消状态：-1：订单无效0:采购单有效1:采购单取消");
        mapper.put("confirm_state","确认状态：0未确认1确认成功2确认失败");
        mapper.put("purchase_mode","采购单下单模式1自动模式0人工模式");
        mapper.put("jd_order_id","京东订单号");
        mapper.put("jd_province","转换后的京东省份编码");
        mapper.put("jd_city","转换后的京东城市编码");
        mapper.put("jd_county","转换后的京东区县编码");
        mapper.put("jd_town","转换后的京东乡镇编码");
        mapper.put("jd_pin","转换后的京东pin");
        mapper.put("jd_client_id","京东clientId");
        mapper.put("pay_status","支付状态：1:未支付；2:已支付；3:未知");
        mapper.put("settle_status","结算状态：1:未结算；2:处理中；6:已结算");
        return mapper;
    }

    private static Map<String, String> getEntityMapper() {
        Map<String, String> mapper = Maps.newLinkedHashMap();
        mapper.put("id","主键");
        mapper.put("order_id","po订单id");
        mapper.put("px_order_id","采购平台订单号");
        mapper.put("product_no","商品编号");
        mapper.put("product_name","商品名称");
        mapper.put("product_num","商品数量");
        mapper.put("unit_price","商品销售单价");
        mapper.put("price_type","价格类型");
        mapper.put("px_price","平台结算价");
        mapper.put("tax_price","商品税价");
        mapper.put("naked_price","商品祼价");
        mapper.put("tax_rate","税率");
        mapper.put("total_price","含税总价");
        mapper.put("total_tax_price","总税额");
        mapper.put("total_naked_price","不含税总价");
        mapper.put("material_no","物料编码");
        mapper.put("created","创建时间");
        mapper.put("modified","更新时间");
        mapper.put("status","是否有效，1:有效,0:无效");
        mapper.put("version","版本号，更新自增1");
        mapper.put("remark","备注信息");
        mapper.put("cancel_state","取消状态-1:无效0：未取消，有效1：取消");
        return mapper;
    }

    private static Map<String, String> getErrInfoMapper() {
        Map<String, String> mapper = Maps.newLinkedHashMap();
        mapper.put("id","主键");
        mapper.put("order_id","po订单id");
        mapper.put("px_order_id","采购平台订单号");
        mapper.put("error_type","异常消息类型：1采购单异常信息2商品异常信息");
        mapper.put("error_code","返回错误码");
        mapper.put("error_desc","错误信息描述");
        mapper.put("product_no","商品编号");
        mapper.put("product_num","商品数量");
        mapper.put("err_descript","商品错误表述");
        mapper.put("created","创建时间");
        mapper.put("modified","更新时间");
        mapper.put("status","是否有效，1:有效,0:无效");
        mapper.put("version","版本号，更新自增1");
        mapper.put("remark","备注信息");
        return mapper;
    }
}
