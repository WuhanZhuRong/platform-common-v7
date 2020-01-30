package com.zhurong.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

//    private static final String HTTP = "http";
//
//    @Value("#{'${elasticsearch.hosts}'.split(',')}")
//    private  List<String> hosts;
//
//
//    @Bean
//    @Primary
//    public RestHighLevelClient client(){
//        ArrayList<HttpHost> arrHost = new ArrayList<HttpHost>();
//        for (String host : hosts){
//            String[] split = host.split(":");
////            arrHost.add(new HttpHost(split[0], split.length == 2 ? Integer.parseInt(split[1]) : 80, HTTP));
//            arrHost.add(new HttpHost(split[0], -1, HTTP));
//        }
//
//        return new RestHighLevelClient(
//                RestClient.builder(arrHost.toArray(new HttpHost[hosts.size()])));
//
//    }

}
