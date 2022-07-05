#### 学习项目，学习参考 https://github.com/chenqian56131/spring-boot-starter-canal
### 使用办法：
1、先将项目使用maven install转换为maven依赖放在本地仓库  
2、将依赖引入pom文件中  
3、在自己项目的配置文件中配置  
canal:  
&emsp;client:  
&emsp;&emsp;instances:  
&emsp;&emsp;&emsp;&emsp;localTest: # (这个实例名，必须是配置在canal server) 
&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;host: 127.0.0.1  
&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;port: 11111  
4、自定义一个canal事件监听器  
@Component
public class CanalListener implements CanalEventListener {

    @Override
    public void onEvent(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        System.out.println("=============监听到canal");
        System.out.println(eventType);
        System.out.println(rowData);
    }
}

5、在SpringBoot启动类上，写上注解 @EnableCanalClient  

6、配置canal server，启动canal server  

### 未来改进点
1、基于MQ的信息监听。
