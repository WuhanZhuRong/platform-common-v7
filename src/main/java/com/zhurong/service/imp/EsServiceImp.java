package com.zhurong.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhurong.bean.Hospital;
import com.zhurong.bean.NoticeVo;
import com.zhurong.bean.User;
import com.zhurong.service.EsService;
import com.zhurong.util.DateTimeUtil;
import com.zhurong.util.PageResult;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EsServiceImp implements EsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EsServiceImp.class);
    //    private static final String USER = "user";
    private static final String USER = "index";
    private static final String USER1 = "user1";
    private static final String YMD = "yyyy-MM-dd";
    
    private static final String NOTICE = "notice";
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    private ObjectMapper objectMapper;
    
    @Autowired
    public EsServiceImp(RestHighLevelClient restHighLevelClient, ObjectMapper objectMapper){
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public PageResult<User> getAll(Integer page, Integer size) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        return getPageResult(page, size, searchSourceBuilder);
    }

    @Override
    public PageResult<Hospital> getHospAll(Integer page, Integer size) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        return getHospPageResult(page, size, searchSourceBuilder);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void save(User user) {
        Map<String, Object> map = objectMapper.convertValue(user, Map.class);
        IndexRequest indexRequest = new IndexRequest(USER1).id(user.getId()).source(map);
        try {
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delById(Integer id) {
        DeleteRequest deleteRequest = new DeleteRequest(USER1, id.toString());
        try {
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PageResult<User> findByNameLike(Integer page, Integer size, String criteria) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("name", criteria));
        return getPageResult(page, size, searchSourceBuilder);
    }

    @Override
    public PageResult<Hospital> findHospByCity(Integer page, Integer size, String city) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("city", city));
        return getHospPageResult(page, size, searchSourceBuilder);
    }

    @Override
    public PageResult<Hospital> findHospitalList(Integer page, Integer size, String city,
                                                 ArrayList<String> supplies, ArrayList<String> catagories) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = city != null ?
                QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery("city", city)) :
                QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery("city", "市"));

        if (!catagories.isEmpty()) {
            for (String catagory : catagories) {
                if (catagory != null) {
                    boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("supplies.name", catagory));
                }
            }
        }

        if (!supplies.isEmpty()) {
            for (String supply : supplies) {
                if (supply != null) {
                    boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("supplies.name", supply));
                }
            }
        }

        searchSourceBuilder.query(boolQueryBuilder);
        return getHospPageResult(page, size, searchSourceBuilder);
    }

    @Override
    public PageResult<Hospital> findHospital(String id) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("id", id));
        return getHospPageResult(1, 1, searchSourceBuilder);
    }

    @Override
    public PageResult<User> search(Integer page, Integer size, String criteria) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.queryStringQuery(criteria));
        return getPageResult(page, size, searchSourceBuilder);
    }

    @Override
    public PageResult<User> search(Integer page, Integer size, String name, Integer age, String start, String end) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(!StringUtils.isEmpty(name)){
            boolQueryBuilder.must(QueryBuilders.matchQuery("name", name));
        }
        if(!StringUtils.isEmpty(age)){
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("age", age));
        }
        start = dateDispose(start);
        end = dateDispose(end);
        boolQueryBuilder.must(QueryBuilders.rangeQuery("birthday").format(YMD).from(start).to(end));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.highlighter(getHighlightBuilder());
        return getPageResult(page, size, searchSourceBuilder);
    }

    @Override
    public void createIndex(String index) {
        CreateIndexRequest createIndexRequest= new CreateIndexRequest(index);

        //分词器start
        Map<String, Object> ik_smart_pinyin = new HashMap<>();
        ik_smart_pinyin.put("type", "custom");
        ik_smart_pinyin.put("tokenizer", "ik_smart");
        String[] filterArr = {"my_pinyin", "word_delimiter"};
        ik_smart_pinyin.put("filter", filterArr);
        Map<String, Object> ik_max_word_pinyin = new HashMap<>();
        ik_max_word_pinyin.put("type", "custom");
        ik_max_word_pinyin.put("tokenizer", "ik_max_word");
        ik_max_word_pinyin.put("filter", filterArr);
        Map<String, Object> analyzer = new HashMap<>();
        analyzer.put("ik_smart_pinyin", ik_smart_pinyin);

        analyzer.put("ik_max_word_pinyin", ik_max_word_pinyin);
        //分词器end

        //过滤器start
        Map<String, Object> my_pinyin = new HashMap<>();
        my_pinyin.put("type", "pinyin");
        my_pinyin.put("first_letter", "prefix");
        my_pinyin.put("padding_char", "");
        Map<String, Object> filter = new HashMap<>();
        filter.put("my_pinyin", my_pinyin);
        //过滤器end

        //settings start
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("filter", filter);
        analysis.put("analyzer", analyzer);
        Map<String, Object> map = new HashMap<>();
        map.put("analysis",analysis);
        map.put("number_of_shards", 5);
        map.put("number_of_replicas", 2);
        createIndexRequest.settings(map);
        //settings end

        //mappings start
        Map<String, Object> name = new HashMap<>();
        name.put("type", "text");
        name.put("analyzer", "ik_smart_pinyin");
        Map<String, Object> age = new HashMap<>();
        age.put("type", "integer");
        Map<String, Object> birthday = new HashMap<>();
        birthday.put("type", "date");
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", name);
        properties.put("age", age);
        properties.put("birthday", birthday);
        Map<String, Object> _source = new HashMap<>();
        _source.put("enabled", true);
        Map<String, Object> _doc = new HashMap<>();
        _doc.put("_source", _source);
        _doc.put("properties", properties);
        createIndexRequest.mapping(_doc);
        //mappings end

        try {
            restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        }catch (ConnectException a){
            LOGGER.error("未连接");
        }catch (RuntimeException b){
            b.printStackTrace();
            LOGGER.error("返回错误");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String delIndex(String index) {
        //判断索引是否存在
        if(!existsIndex(index)){
            return "索引不存在";
        }
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
        try {
            AcknowledgedResponse delete = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            if(delete.isAcknowledged()){
                return "删除成功";
            }else{
                return "删除失败";
            }

        }catch (ConnectException a){
            LOGGER.error("未连接");
        }catch (RuntimeException b){
            b.printStackTrace();
            LOGGER.error("返回错误");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "删除失败";
    }

    private boolean existsIndex(String index){
        GetIndexRequest request = new GetIndexRequest(index);
        try {
            return restHighLevelClient.indices().exists(request,RequestOptions.DEFAULT);
        }catch (ConnectException a){
            LOGGER.error("未连接");
        }catch (RuntimeException b){
            b.printStackTrace();
            LOGGER.error("返回错误");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private PageResult<User> getPageResult(Integer page, Integer size, SearchSourceBuilder searchSourceBuilder) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(USER);
        //分页 start
        searchSourceBuilder.size(size);
        searchSourceBuilder.from((page-1)*size);
        //分页 end
        //排序 start
        searchSourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
        searchSourceBuilder.sort(new FieldSortBuilder("_id").order(SortOrder.ASC));
        //排序 end
        searchRequest.source(searchSourceBuilder);
        System.out.println(searchSourceBuilder.toString());
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //封装查询结果 start
            SearchHit[] hits = searchResponse.getHits().getHits();
            PageResult<User> pageResult = new PageResult<>();
            for (SearchHit hit : hits){
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField nameField = highlightFields.get("name");
                if (nameField != null){
                    Text[] texts = nameField.fragments();
                    StringBuilder name = new StringBuilder();
                    for (Text str : texts){
                        name.append(str);
                    }
                    sourceAsMap.put("name", name.toString());
                }
                pageResult.getData().add(objectMapper.convertValue(sourceAsMap, User.class));
            }
            pageResult.setPageNo(page);
            pageResult.setPageSize(size);
            pageResult.setTotalCount(searchResponse.getHits().getTotalHits().value);
            pageResult.setPageCount((long)Math.ceil(pageResult.getTotalCount()/(size+0.0)));
            pageResult.setHasNextPage(page < pageResult.getPageCount());
            pageResult.setHasPreviousPage(page > 1);
            //封装查询结果 end
            return pageResult;
        }catch (ConnectException a){
            LOGGER.error("未连接");
        }catch (RuntimeException b){
            b.printStackTrace();
            LOGGER.error("返回错误");
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PageResult<Hospital> getHospPageResult(Integer page, Integer size, SearchSourceBuilder searchSourceBuilder) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(USER);
        //分页 start
        searchSourceBuilder.size(size);
        searchSourceBuilder.from((page-1)*size);
        //分页 end
        //排序 start
        searchSourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
        searchSourceBuilder.sort(new FieldSortBuilder("_id").order(SortOrder.ASC));
        //排序 end
        searchRequest.source(searchSourceBuilder);
        System.out.println(searchSourceBuilder.toString());
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //封装查询结果 start
            SearchHit[] hits = searchResponse.getHits().getHits();
            PageResult<Hospital> pageResult = new PageResult<>();
            for (SearchHit hit : hits){
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField nameField = highlightFields.get("name");
                if (nameField != null){
                    Text[] texts = nameField.fragments();
                    StringBuilder name = new StringBuilder();
                    for (Text str : texts){
                        name.append(str);
                    }
                    sourceAsMap.put("name", name.toString());
                }
                pageResult.getData().add(objectMapper.convertValue(sourceAsMap, Hospital.class));
            }
            pageResult.setPageNo(page);
            pageResult.setPageSize(size);
            pageResult.setTotalCount(searchResponse.getHits().getTotalHits().value);
            pageResult.setPageCount((long)Math.ceil(pageResult.getTotalCount()/(size+0.0)));
            pageResult.setHasNextPage(page < pageResult.getPageCount());
            pageResult.setHasPreviousPage(page > 1);
            //封装查询结果 end
            return pageResult;
        }catch (ConnectException a){
            LOGGER.error("未连接");
        }catch (RuntimeException b){
            b.printStackTrace();
            LOGGER.error("返回错误");
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PageResult<Hospital> findNearbyHospitals(double latitude, double longitude, double distance, int page, int size) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        GeoDistanceQueryBuilder distanceQueryBuilder = new GeoDistanceQueryBuilder("location");
        distanceQueryBuilder.point(latitude, longitude);
        distanceQueryBuilder.distance(distance, DistanceUnit.KILOMETERS);
        distanceQueryBuilder.geoDistance(GeoDistance.PLANE);
        searchSourceBuilder.postFilter(distanceQueryBuilder);

        GeoDistanceSortBuilder distanceSortBuilder =
                new GeoDistanceSortBuilder("location", latitude, longitude);
        distanceSortBuilder.unit(DistanceUnit.KILOMETERS);
        distanceSortBuilder.order(SortOrder.ASC);
        searchSourceBuilder.sort(distanceSortBuilder);
        searchSourceBuilder.size(size);
        searchSourceBuilder.from((page-1)*size);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(USER);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //封装查询结果 start
            SearchHit[] hits = searchResponse.getHits().getHits();
            PageResult<Hospital> pageResult = new PageResult<>();
            for (SearchHit hit : hits){
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField nameField = highlightFields.get("name");
                if (nameField != null){
                    Text[] texts = nameField.fragments();
                    StringBuilder name = new StringBuilder();
                    for (Text str : texts){
                        name.append(str);
                    }
                    sourceAsMap.put("name", name.toString());
                }
                pageResult.getData().add(objectMapper.convertValue(sourceAsMap, Hospital.class));
            }
            pageResult.setPageNo(page);
            pageResult.setPageSize(size);
            pageResult.setTotalCount(searchResponse.getHits().getTotalHits().value);
            pageResult.setPageCount((long)Math.ceil(pageResult.getTotalCount()/(size+0.0)));
            pageResult.setHasNextPage(page < pageResult.getPageCount());
            pageResult.setHasPreviousPage(page > 1);
            //封装查询结果 end
            return pageResult;
        } catch (IOException e) {
            LOGGER.error("findNearbyHospitals error");
        }
        return null;
    }

    public Map<String, Long> getTotalDemands() {
        List<Hospital> hospitals = new ArrayList<>();
        int querySize = 1000;
        int queryPage = 1;
        boolean continueFlag = true;
        while (continueFlag) {
            PageResult<Hospital> pageResult = getHospAll(queryPage, querySize);
            hospitals.addAll(pageResult.getData());
            continueFlag = pageResult.isHasNextPage();
            queryPage++;
        }
        Map<String, Long> totalDemands = new HashMap<>();
        for (Hospital hospital : hospitals) {
            List<Map<String, String>> supplies = hospital.getSupplies();
            for (Map<String, String> supply : supplies) {
                if (!supply.containsKey("amount") || !supply.containsKey("name")
                        || Long.parseLong(supply.get("amount")) <= 0L)
                    continue;
                String name = supply.get("name");
                Long current = totalDemands.getOrDefault(name, 0L);
                totalDemands.put(name, current + Long.parseLong(supply.get("amount")));
            }
        }
        return totalDemands;
    }

    private HighlightBuilder getHighlightBuilder (){
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        return highlightBuilder;
    }
    private String dateDispose(String date){
        if(StringUtils.isEmpty(date) || !DateTimeUtil.isDateStr(date, YMD)){
            return null;
        }
        return DateTimeUtil.parseDateToString(DateTimeUtil.parseStringToDate(date, YMD), YMD);
    }

	@Override
	public PageResult<NoticeVo> findNotice(String id) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("_id", id));
        return getNoticePageResult(1, 1, searchSourceBuilder);
	}
	
	private PageResult<NoticeVo> getNoticePageResult(Integer page, Integer size, SearchSourceBuilder searchSourceBuilder) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(NOTICE);
        searchRequest.source(searchSourceBuilder);
        System.out.println("query:"+searchSourceBuilder.toString());
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //封装查询结果 start
            SearchHit[] hits = searchResponse.getHits().getHits();
            PageResult<NoticeVo> pageResult = new PageResult<>();
            for (SearchHit hit : hits){
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField nameField = highlightFields.get("name");
                if (nameField != null){
                    Text[] texts = nameField.fragments();
                    StringBuilder name = new StringBuilder();
                    for (Text str : texts){
                        name.append(str);
                    }
                    sourceAsMap.put("name", name.toString());
                }
                pageResult.getData().add(objectMapper.convertValue(sourceAsMap, NoticeVo.class));
            }
            pageResult.setPageNo(page);
            pageResult.setPageSize(size);
            pageResult.setTotalCount(searchResponse.getHits().getTotalHits().value);
            pageResult.setPageCount((long)Math.ceil(pageResult.getTotalCount()/(size+0.0)));
            pageResult.setHasNextPage(page < pageResult.getPageCount());
            pageResult.setHasPreviousPage(page > 1);
            //封装查询结果 end
            return pageResult;
        }catch (ConnectException a){
            LOGGER.error("未连接");
        }catch (RuntimeException b){
            b.printStackTrace();
            LOGGER.error("返回错误");
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	
}
