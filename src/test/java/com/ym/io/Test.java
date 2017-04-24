package com.ym.io;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.Random;

/**
 * 测试方法
 * 
 * @author yangtao__anxpp.com
 * @version 1.0
 */
public class Test {


	@org.junit.Test
	public void  test() {
		String text = "{\"oilTax\":0,\"depTerminal\":\"T2\",\"addDay\":\"0\",\"arrCity\":\"NKG\",\"airCompanyDataFlag\":\"2\",\"flightNo\":\"MU2802\",\"planeStyleCN\":\"中型机\",\"depTimeType\":\"1\",\"arrTime\":\"1230\",\"planeStyle\":\"320\",\"bingoLeastClassInfo\":{\"oilTax\":0,\"classLevel\":\"1\",\"uniqueKey\":\"\",\"sourceId\":\"588586\",\"classNoCn\":\"经济舱\",\"airCompanyFlag\":false,\"classNo\":\"E\",\"policyId\":\"83DB939FDEC7DCCEC7C3C1C6C0C2C6C4DAD5D8888CDFC4CEC5C1C2DDC4D9D4948B89D8C1C5CECED1C0DDD0929581D4CDCDC9D4CBD0DF9F9984D3C8C3DAC5DAD59B988ED9C6CDD0CFDCD3919580D7CCC7D6C9D6D9929B9ADDCAC1DED186939291DAC3CBCECCCDD0CFDCD380919F93D4CDC9CCCACBD2CDD2DD969896D1CED7C7C5CDCDCACBA3CCDCD3D2959CD1CEC4DAD588DBC0CAC9CDCE82\",\"seatNum\":\"A\",\"seatType\":\"2\",\"productCode\":\"\",\"venderPrice\":1500,\"childOilTax\":0,\"checkContent\":\"3D80812258170A36D5BF5EDD9994B2ABMU2802PEKNKG2017-04-2315001500185050001500000150000013D80812258170A36D5BF5EDD9994B2AB\",\"childSalePrice\":930,\"fareItemId\":\"125400_1\",\"childVenderPrice\":930,\"promotionPrice\":0,\"discount\":81,\"childSeatCode\":\"Y\",\"price\":1500,\"originalPrice\":1500,\"saleDiscountMap\":[{\"entry\":{\"int\":1,\"SaleDiscount\":{\"saleDiscountMoney\":0,\"saleDiscountType\":1,\"saleDiscountPrice\":1500,\"saleDiscountName\":\"直减优惠\",\"saleDiscountText\":\"直减优惠text\"}}}],\"fullPrice\":1850,\"discountFlag\":false},\"planeStyleType\":\"2\",\"depTime\":\"1025\",\"arrDate\":\"2017-04-23\",\"depDate\":\"2017-04-23\",\"airways\":\"MU\",\"isStop\":\"0\",\"depAirdrome\":\"首都机场\",\"childOilTax\":0,\"depCity\":\"PEK\",\"tax\":50,\"arrAirdrome\":\"禄口机场\",\"arrTerminal\":\"T2\",\"bingoClassInfoList\":[{\"FlightClassInfo\":[{\"oilTax\":0,\"classLevel\":\"1\",\"uniqueKey\":\"\",\"sourceId\":\"588586\",\"classNoCn\":\"经济舱\",\"airCompanyFlag\":false,\"classNo\":\"E\",\"policyId\":\"83DB939FDEC7DCCEC7C3C1C6C0C2C6C4DAD5D8888CDFC4CEC5C1C2DDC4D9D4948B89D8C1C5CECED1C0DDD0929581D4CDCDC9D4CBD0DF9F9984D3C8C3DAC5DAD59B988ED9C6CDD0CFDCD3919580D7CCC7D6C9D6D9929B9ADDCAC1DED186939291DAC3CBCECCCDD0CFDCD380919F93D4CDC9CCCACBD2CDD2DD969896D1CED7C7C5CDCDCACBA3CCDCD3D2959CD1CEC4DAD588DBC0CAC9CDCE82\",\"seatNum\":\"A\",\"seatType\":\"2\",\"productCode\":\"\",\"venderPrice\":1500,\"childOilTax\":0,\"checkContent\":\"3D80812258170A36D5BF5EDD9994B2ABMU2802PEKNKG2017-04-2315001500185050001500000150000013D80812258170A36D5BF5EDD9994B2AB\",\"childSalePrice\":930,\"fareItemId\":\"125400_1\",\"childVenderPrice\":930,\"promotionPrice\":0,\"discount\":81,\"childSeatCode\":\"Y\",\"price\":1500,\"originalPrice\":1500,\"saleDiscountMap\":[{\"entry\":{\"int\":1,\"SaleDiscount\":{\"saleDiscountMoney\":0,\"saleDiscountType\":1,\"saleDiscountPrice\":1500,\"saleDiscountName\":\"直减优惠\",\"saleDiscountText\":\"直减优惠text\"}}}],\"fullPrice\":1850,\"discountFlag\":false},{\"oilTax\":0,\"classLevel\":\"1\",\"uniqueKey\":\"\",\"sourceId\":\"588586\",\"classNoCn\":\"经济舱\",\"airCompanyFlag\":false,\"classNo\":\"M\",\"policyId\":\"83DB939FDEC7DCCEC7C3C1C6C0C2C6C4DAD5D8888CDFC4CEC6C9C2DDC4D9D4948B89D8C1C5CECED1C0DDD0929581D4CDCDC9D4CBD0DF9F9984D3C8C3DAC5DAD59B988ED9C6CDD0CFDCD3919580D7CCC7D6C9D6D9929B9ADDCAC1DED186939291DAC3CBCDC4CDD0CFDCD380919F93D4CDC9CFC2CBD2CDD2DD969896D1CED7C7C5CDCAC3C2A3CCDCD3D2959CD1CEC4DAD588DBC0CACAC5CE82\",\"seatNum\":\"A\",\"seatType\":\"2\",\"productCode\":\"\",\"venderPrice\":1680,\"childOilTax\":0,\"checkContent\":\"3D80812258170A36D5BF5EDD9994B2ABMU2802PEKNKG2017-04-2316801680185050001680000168000013D80812258170A36D5BF5EDD9994B2AB\",\"childSalePrice\":930,\"fareItemId\":\"125399_1\",\"childVenderPrice\":930,\"promotionPrice\":0,\"discount\":90,\"childSeatCode\":\"Y\",\"price\":1680,\"originalPrice\":1680,\"saleDiscountMap\":[{\"entry\":{\"int\":1,\"SaleDiscount\":{\"saleDiscountMoney\":0,\"saleDiscountType\":1,\"saleDiscountPrice\":1680,\"saleDiscountName\":\"直减优惠\",\"saleDiscountText\":\"直减优惠text\"}}}],\"fullPrice\":1850,\"discountFlag\":false},{\"oilTax\":0,\"classLevel\":\"1\",\"uniqueKey\":\"\",\"sourceId\":\"588586\",\"classNoCn\":\"全价经济舱\",\"airCompanyFlag\":false,\"classNo\":\"Y\",\"policyId\":\"83DB939FDEC7DCCEC7C3C1C6C0C2C6C4DAD5D8888CDFC4CEC8C4C2DDC4D9D4948B89D8C1C5CECED1C0DDD0929581D4CDCDC9D4CBD0DF9F9984D3C8C3DAC5DAD59B988ED9C6CDD0CFDCD3919580D7CCC7D6C9D6D9929B9ADDCAC1DED186939291DAC3CBC3C9CDD0CFDCD380919F93D4CDC9C1CFCBD2CDD2DD969896D1CED7C7C5CDCAC3CCA3CCDCD3D2959CD1CEC4DAD588DBC0CAC4C8CE82\",\"seatNum\":\"A\",\"seatType\":\"2\",\"productCode\":\"\",\"venderPrice\":1850,\"childOilTax\":0,\"checkContent\":\"3D80812258170A36D5BF5EDD9994B2ABMU2802PEKNKG2017-04-2318501850185050001850000185000013D80812258170A36D5BF5EDD9994B2AB\",\"childSalePrice\":930,\"fareItemId\":\"125397_1\",\"childVenderPrice\":930,\"promotionPrice\":0,\"discount\":100,\"childSeatCode\":\"Y\",\"price\":1850,\"originalPrice\":1850,\"saleDiscountMap\":[{\"entry\":{\"int\":1,\"SaleDiscount\":{\"saleDiscountMoney\":0,\"saleDiscountType\":1,\"saleDiscountPrice\":1850,\"saleDiscountName\":\"直减优惠\",\"saleDiscountText\":\"直减优惠text\"}}}],\"fullPrice\":1850,\"discountFlag\":false},{\"oilTax\":0,\"classLevel\":\"2\",\"uniqueKey\":\"\",\"sourceId\":\"588586\",\"classNoCn\":\"头等舱\",\"airCompanyFlag\":false,\"classNo\":\"F\",\"policyId\":\"82D89298DFC4DDC1C6C0C0C1C1C1C7CBDBD6D98F8DDCC5C5C4C7C3DAC5DAD59B8A8AD9C6CFC9C7C0DFC2DFD6949783DAC3CFCBD2CDD2DD919786D1CEC5D8C7D4DB999A88DFC4CFDEC1DED1979382D5C2C9D4CBD0DF909994D3C8C3D8D784919C9FD8C1C9C8CBCFDEC1DED186979D91DAC3CFCEC9CDD0CFDCD3949A90D7CCD5C9CBCFC8C5CCA1CED2DDD0979AD7CCC6D4DB8AD9C6C8CBCAC08C\",\"seatNum\":\"4\",\"seatType\":\"2\",\"productCode\":\"\",\"venderPrice\":5550,\"childOilTax\":0,\"checkContent\":\"3D80812258170A36D5BF5EDD9994B2ABMU2802PEKNKG2017-04-2355505550185050005550000555000013D80812258170A36D5BF5EDD9994B2AB\",\"childSalePrice\":2780,\"fareItemId\":\"125391_1\",\"childVenderPrice\":2780,\"promotionPrice\":0,\"discount\":100,\"childSeatCode\":\"F\",\"price\":5550,\"originalPrice\":5550,\"saleDiscountMap\":[{\"entry\":{\"int\":1,\"SaleDiscount\":{\"saleDiscountMoney\":0,\"saleDiscountType\":1,\"saleDiscountPrice\":5550,\"saleDiscountName\":\"直减优惠\",\"saleDiscountText\":\"直减优惠text\"}}}],\"fullPrice\":1850,\"discountFlag\":false}]}],\"fullPrice\":1850,\"airwaysCn\":\"东方航空\"}";
		JSONObject parent = JSONObject.parseObject(text);
		JSONObject bingoLeastClassInfo = parent.getJSONObject("bingoLeastClassInfo");
		bingoLeastClassInfo.remove("airCompanyFlag");
		JSONArray bingoClassInfoList = parent.getJSONArray("bingoClassInfoList");
		for (Object o : bingoClassInfoList) {
			JSONArray array = ((JSONObject) o).getJSONArray("FlightClassInfo");
			for (Object item : array) {
				JSONObject obj = ((JSONObject) item);
				obj.remove("airCompanyFlag");
			}
		}
		System.out.println(parent);

	}

	// 测试主方法
	public static void main(String[] args) throws InterruptedException {
		// 运行服务器
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ServerBetter.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		// 避免客户端先于服务器启动前执行代码
		Thread.sleep(100);
		// 运行客户端
		final char operators[] = { '+', '-', '*', '/' };
		final Random random = new Random(System.currentTimeMillis());
		new Thread(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				while (true) {
					// 随机产生算术表达式
					String expression = random.nextInt(10) + "" + operators[random.nextInt(4)]
							+ (random.nextInt(10) + 1);
					Client.send(expression);
					try {
						Thread.currentThread().sleep(random.nextInt(1000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
