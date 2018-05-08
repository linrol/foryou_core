package foryou.core.common;

/**
 * 分页插件
 * 
 * @author 罗林
 *
 */
public class Paginate {
	public int start = 0;
	public int limit = 10;
	public boolean isSearch = false;
	public long totalCount;
	public long pageCount;

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public boolean isSearch() {
		return isSearch;
	}

	public void setSearch(boolean isSearch) {
		this.isSearch = isSearch;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public long getPageCount(int limit) {
		return totalCount % limit == 0 ? (totalCount / limit) : (totalCount / limit) + 1;
	}

	public void setPageCount(long pageCount) {
		this.pageCount = pageCount;
	}
}
