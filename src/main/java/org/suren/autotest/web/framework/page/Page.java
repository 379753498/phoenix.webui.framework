/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.suren.autotest.web.framework.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Alert;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.suren.autotest.web.framework.core.Keyboard;
import org.suren.autotest.web.framework.core.Mouse;
import org.suren.autotest.web.framework.core.ui.Button;
import org.suren.autotest.web.framework.data.DynamicData;
import org.suren.autotest.web.framework.selenium.SeleniumEngine;
import org.suren.autotest.web.framework.util.StringUtils;

/**
 * 对HTML页面的逻辑封装，不一定是一一对应
 * @author suren
 * @date Jul 17, 2016 9:06:52 AM
 */
public class Page
{
	/** 页面唯一标示 */
	private String			id;
	/** 当前页面的url地址 */
	private String			url;
	/** 当前页面所关联的数据源 */
	private String			dataSource;
	/** 用于保存元素对象相关的数据 */
	private Map<String, Object>				data = new HashMap<String, Object>();
	private String paramPrefix;

	@Autowired
	private SeleniumEngine	engine;
	@Autowired
	private Mouse mouse;
	@Autowired
	private Keyboard keyboard;
	
	/** 通用的按钮 */
	@Autowired
	private Button commonBut;
	
	@Autowired
	private List<DynamicData> dynamicDataList;

	/**
	 * 打开（进入）当前页面
	 */
	public final void open()
	{
		String paramUrl = paramTranslate(url);
		
		engine.openUrl(paramUrl);
		
		try
		{
			engine.computeToolbarHeight();
		}
		catch(UnhandledAlertException e)
		{
			Alert alert = engine.getDriver().switchTo().alert();
			if(alert != null)
			{
				alert.dismiss();

				engine.computeToolbarHeight();
			}
		}
	}

	/**
	 * 关闭当前页面
	 */
	public final void close()
	{
		engine.close();
	}
	
	/**
	 * 关闭当前窗口以外的所有窗口并切换到当前窗口
	 */
	public final void closeOthers()
	{
		String currentTitle = getTitle();
		String currentWinHandle = engine.getDriver().getWindowHandle();
		
		for(String winHandle : engine.getDriver().getWindowHandles())
		{
			WebDriver itemDrive = engine.getDriver().switchTo().window(winHandle);
			if(!itemDrive.getTitle().equals(currentTitle))
			{
				itemDrive.close();
			}
		}
		
		engine.getDriver().switchTo().window(currentWinHandle);
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return 给当前对象设置的url地址
	 */
	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}
	
	/**
	 * @return 获取当前页面的url地址
	 */
	public final String getCurrentUrl()
	{
		return engine.getDriver().getCurrentUrl();
	}
	
	/**
	 * @return 当前页面源码
	 */
	public final String getPageSource()
	{
		return engine.getDriver().getPageSource();
	}

	/**
	 * @return 当前页面的title
	 */
	public final String getTitle()
	{
		return engine.getDriver().getTitle();
	}

	public String getDataSource()
	{
		return dataSource;
	}

	public void setDataSource(String dataSource)
	{
		this.dataSource = dataSource;
	}
	
	/**
	 * 添加数据
	 * @param key
	 * @param value
	 */
	public void putData(String key, Object value)
	{
		data.put(key, value);
	}
	
	public void putAllData(Map<String, Object> allData)
	{
		data.putAll(allData);
	}
	
	/**
	 * 移出数据
	 * @param key
	 */
	public void removeData(String key)
	{
		data.remove(key);
	}
	
	/**
	 * @param key
	 * @return 是否包含指定key的数据
	 */
	public boolean containsKey(String key)
	{
		return data.containsKey(key);
	}
	
	/**
	 * 清空数据
	 */
	public void clearData()
	{
		data.clear();
	}
	
	/**
	 * @param value
	 * @return
	 */
	public String paramTranslate(String value)
	{
		String result = value;
		for(DynamicData dynamicData : dynamicDataList)
		{
			if("system".equals(dynamicData.getType()))
			{
				result = dynamicData.getValue(result);
				break;
			}
		}
		
		result = StringUtils.paramTranslate(data, getParamPrefix(), result);
		
		return result;
	}

	/**
	 * @return the mouse
	 */
	public Mouse getMouse()
	{
		return mouse;
	}

	/**
	 * @return the keyboard
	 */
	public Keyboard getKeyboard()
	{
		return keyboard;
	}

	public Button getCommonBut()
	{
		return commonBut;
	}

	public void setCommonBut(Button commonBut)
	{
		this.commonBut = commonBut;
	}

	public String getParamPrefix()
	{
		return paramPrefix;
	}

	public void setParamPrefix(String paramPrefix)
	{
		this.paramPrefix = paramPrefix;
	}
}
