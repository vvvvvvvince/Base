package com.weaver.util.dev.json;

import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @description
 * @Author slfang
 * @Time 2019/1/4 12:47
 * @Version 1.0
 **/
public class Page<T> implements Serializable {
        public static final String ASC = "asc";
        public static final String DESC = "desc";
        public static int DEFAULT_PAGE_SIZE = 10;
        protected int currentPageNo;
        protected int pageSize;
        protected List<T> result;
        protected long totalCount;
        protected boolean autoCount;
        protected String pageUrl;
        protected String formName;
        protected String orderBy;
        protected String order;
        private long start;

        public Page(int pageSize) {
            this.currentPageNo = 1;
            this.pageSize = DEFAULT_PAGE_SIZE;
            this.result = Collections.emptyList();
            this.totalCount = -1L;
            this.autoCount = true;
            this.pageUrl = "errorPage.jsp";
            this.setPageSize(pageSize);
        }

        public Page(int pageSize, boolean autoCount) {
            this.currentPageNo = 1;
            this.pageSize = DEFAULT_PAGE_SIZE;
            this.result = Collections.emptyList();
            this.totalCount = -1L;
            this.autoCount = true;
            this.pageUrl = "errorPage.jsp";
            this.setPageSize(pageSize);
            this.setAutoCount(autoCount);
        }

        public Page() {
            this(DEFAULT_PAGE_SIZE);
        }

        public Page(long start, long totalSize, int pageSize, List<T> data) {
            this.currentPageNo = 1;
            this.pageSize = DEFAULT_PAGE_SIZE;
            this.result = Collections.emptyList();
            this.totalCount = -1L;
            this.autoCount = true;
            this.pageUrl = "errorPage.jsp";
            this.pageSize = pageSize;
            this.start = start;
            this.totalCount = totalSize;
            this.result = data;
        }

        public long getTotalPageCount() {
            Assert.isTrue(this.pageSize > 0);
            return this.totalCount % (long)this.pageSize == 0L ? this.totalCount / (long)this.pageSize : this.totalCount / (long)this.pageSize + 1L;
        }

        public boolean isOrderBySetted() {
            return StringUtils.isNotBlank(this.orderBy) && StringUtils.isNotBlank(this.order);
        }

        public int getFirstOfPage() {
            return (this.currentPageNo - 1) * this.pageSize + 1;
        }

        public int getLastOfPage() {
            return this.currentPageNo * this.pageSize;
        }

        public static int getDEFAULT_PAGE_SIZE() {
            return DEFAULT_PAGE_SIZE;
        }

        public static void setDEFAULT_PAGE_SIZE(int dEFAULTPAGESIZE) {
            DEFAULT_PAGE_SIZE = dEFAULTPAGESIZE;
        }

        public int getCurrentPageNo() {
            return this.currentPageNo;
        }

        public void setCurrentPageNo(int currentPageNo) {
            this.currentPageNo = currentPageNo;
        }

        public int getPageSize() {
            return this.pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public List<T> getResult() {
            return this.result;
        }

        public void setResult(List<T> result) {
            this.result = result;
        }

        public long getTotalCount() {
            return this.totalCount;
        }

        public void setTotalCount(long totalCount) {
            this.totalCount = totalCount;
        }

        public boolean isAutoCount() {
            return this.autoCount;
        }

        public void setAutoCount(boolean autoCount) {
            this.autoCount = autoCount;
        }

        public String getPageUrl() {
            return this.pageUrl;
        }

        public void setPageUrl(String pageUrl) {
            this.pageUrl = pageUrl;
        }

        public long getStart() {
            return this.start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public String getOrderBy() {
            return this.orderBy;
        }

        public void setOrderBy(String orderBy) {
            this.orderBy = orderBy;
        }

        public String getOrder() {
            return this.order;
        }

        public void setOrder(String order) {
            this.order = order;
        }

        public static String getAsc() {
            return "asc";
        }

        public static String getDesc() {
            return "desc";
        }

        public String getFormName() {
            return this.formName;
        }

        public void setFormName(String formName) {
            this.formName = formName;
        }

        public String getJartJsonResult() {
            return "{total:" + this.getTotalCount() + ",rows:" + JSONArray.fromObject(this.getResult()) + "}";
        }
}
