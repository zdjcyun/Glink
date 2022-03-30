package com.zcloud.alone.network.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @Created: with IDEA
 * @Description:
 * @Author:BradyXu<313582767@qq.com>
 * @Date:Create in 2019/4/10 16:19
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PackageData {

    /**
     * cntdata : [{"method":"NTS301","A02":"2018070002","A04":"01","S01":"023934","S04":"01","V01":"00001520.01","V03":"0000.00"},{"method":"NTS301","A02":"2018070002","A04":"03","S01":"019751","S04":"01","V01":"00001728.53","V03":"0000.00"},{"method":"NTS301","A02":"2018070002","A04":"04","S01":"009016","S04":"01","V01":"00001928.36","V03":"0000.00"}]
     * T04 : 2000-00-01 00:00:06
     */
    @JsonProperty("T04")
    private String dateTime;
    private List<CeZhiModel> cntdata;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public List<CeZhiModel> getCntdata() {
        return cntdata;
    }

    public void setCntdata(List<CeZhiModel> cntdata) {
        this.cntdata = cntdata;
    }

    @Override
    public String toString() {
        return "PackageData{" +
                "dateTime='" + dateTime + '\'' +
                ", cntdata=" + cntdata +
                '}';
    }
}
