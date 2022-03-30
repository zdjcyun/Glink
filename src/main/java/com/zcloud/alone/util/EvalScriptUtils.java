package com.zcloud.alone.util;

import com.zcloud.alone.constant.ScriptConstant;
import com.zcloud.alone.exception.ScriptRunException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 通过脚本引擎的eval方法来执行给定的JavaScript代码
 */
public class EvalScriptUtils {

	private static volatile ScriptEngine scriptEngine;

	public static ScriptEngine getInstance() {
		if (scriptEngine == null) {
			synchronized (EvalScriptUtils.class) {
				if (scriptEngine == null) {
					scriptEngine = getEvalScriptEngine();
				}
			}
		}
		return scriptEngine;
	}
	/**
	 * 执行指定脚本里面的指定方法(如果脚本文件中的方法不存在，则加载一次脚本文件)
	 * @param fileName 指定的脚本文件名称
	 * @param methodName 指定的方法
	 * @param params 方法所需的参数
	 * @return
	 */
    public static String invokeOneLoad(String fileName, String methodName, Object... params) {
    	// 真正的方法名是文件名+通用方法名
    	String realMethodName = fileName.replaceFirst(".js", "_" + methodName);
    	String result = null;
    	try {
    		result = invoke(realMethodName, params);
		} catch (NoSuchMethodException e) {
			loadScriptFile(fileName);
			try {
				result = invoke(realMethodName, params);
			} catch (NoSuchMethodException e1) {
				throw new ScriptRunException(e.getMessage(), "脚本文件" + fileName + "不存在方法" + realMethodName);
			}
		}
    	return result;
    }

    /**
	 * 执行指定的方法
	 * @param methodName 指定的方法
	 * @param params 方法所需的参数
	 * @return
     * @throws NoSuchMethodException
	 */
    private static String invoke(String methodName, Object... params) throws NoSuchMethodException {
    	ScriptEngine engine = getInstance();
        String result = null;
        try {
        	if(engine instanceof Invocable) {
				// 调用merge方法，并传入两个参数
                Invocable invoke = (Invocable)engine;
    			result = String.valueOf(invoke.invokeFunction(methodName, params));
            }
		} catch (ScriptException e) {
			e.printStackTrace();
		}
        return result;
    }

	/**
	 * 执行指定脚本里面的指定方法(每次执行都重新加载一次脚本)
	 * @param fileName 指定的脚本
	 * @param methodName 指定的方法
	 * @param params 方法所需的参数
	 * @return
	 */
    public static String invokeAlwaysLoad(String fileName, String methodName, Object... params) {
    	// 真正的方法名是文件名+通用方法名
    	String realMethodName = fileName.replaceFirst(".js", "_" + methodName);
    	ScriptEngine engine = getInstance();
        Resource resource = new FileSystemResource(ScriptConstant.BASE_SCRIPT_URL + fileName);
        InputStreamReader reader = null;
        String result = null;
        try {
        	reader = new InputStreamReader(resource.getInputStream());
        	engine.eval(reader);
        	if(engine instanceof Invocable) {
				// 调用merge方法，并传入两个参数
                Invocable invoke = (Invocable)engine;
    			result = String.valueOf(invoke.invokeFunction(realMethodName, params));
            }
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			throw new ScriptRunException(e.getMessage(), "脚本文件" + fileName + "不存在方法" + realMethodName);
		} finally {
			try {
				if(null != reader){
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return result;
    }

    /**
     * 将js文件加载到js引擎中
     * @param fileName
     * @return
     */
    public static ScriptEngine loadScriptFile(String fileName) {
    	ScriptEngine engine = getInstance();
    	Resource fileResource = new FileSystemResource(ScriptConstant.BASE_SCRIPT_URL + fileName);
    	InputStreamReader fileReader = null;
		try {
			fileReader = new InputStreamReader(fileResource.getInputStream());
			engine.eval(fileReader);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		} finally {
			try {
        		if(null != fileReader){
        			fileReader.close();
    	        }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return engine;
    }

    private static ScriptEngine getEvalScriptEngine() {
    	ScriptEngine engine = getJavaScriptEngine();
    	Resource commonResource = new FileSystemResource(ScriptConstant.COMMON_SCRIPT_URL);
    	InputStreamReader commonReader = null;
		try {
			commonReader = new InputStreamReader(commonResource.getInputStream());
			engine.eval(commonReader);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		} finally {
			try {
        		if(null != commonReader){
    	        	commonReader.close();
    	        }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return engine;
    }

    private static ScriptEngine getJavaScriptEngine(){
    	 // create a script engine manager
        ScriptEngineManager factory = new ScriptEngineManager();
        // 通过脚本名称获取
        ScriptEngine engine = factory.getEngineByName("JavaScript");
//        //通过文件扩展名获取
//        ScriptEngine engine = factory.getEngineByExtension("js");
//        //通过MIME类型来获取
//        ScriptEngine engine = factory.getEngineByMimeType("text/javascript");
        return engine;
    }
}
