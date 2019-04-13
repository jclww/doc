```
@Data
public class TeamColumn {

    /**
     * 默认查询所有
     */
    private boolean x_ = true;

    private boolean id = false;

    private boolean name = false;

    private boolean userId = false;

    public static TeamColumn getDefault() {
        return new TeamColumn();
    }
    // 可以考虑自定义setXXX 修改x_为false
}
```

```
public class SearchObject implements Serializable {

    private static final long serialVersionUID = -1L;

    private List<Sort> sorts = new LinkedList<Sort>();

    private Integer pageSize;

    private Integer page;

    private Integer offSet;

    public SearchObject() {
        super();
    }

    public SearchObject(SearchObject so) {
        super();
        this.sorts = so.sorts;
        this.pageSize = so.pageSize;
        this.page = so.page;
    }

    public void addSort(Sort sort) {
        sorts.add(sort);
    }

    public List<Sort> getSorts() {
        return sorts;
    }

    public void setSorts(List<Sort> sorts) {
        this.sorts = sorts;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer pageNo) {
        this.page = pageNo;
    }

    public Integer getOffSet() {
        if (page == null || pageSize == null) {
            return null;
        }
        return (page - 1) * pageSize;
    }

    public void setOffSet(Integer offSet) {
        this.offSet = offSet;
    }
}
```

```
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamSO extends SearchObject {
    /**
     * id
     */
    private Integer id;

    private Long userId;
}
```

```
    List<Team> query(@Param("column") TeamColumn column, @Param("so") TeamSO so);
```

```
    <sql id="whereCondition">
        <trim prefix="WHERE" prefixOverrides="AND|OR">
            <if test="so.id!=null">
                and id = #{so.id}
            </if>
            <if test="so.userId!=null">
                and user_id = #{so.userId}
            </if>
        </trim>
    </sql>
    
    <sql id="Select_Column_List">
        <trim prefix="SELECT" prefixOverrides=",">
            <choose>
                <when test="column.x_ != null and column.x_ == false">
                    <if test="column.id!=null and column.id == true">
                        ,id
                    </if>
                    <if test="column.userId!=null and column.userId == true">
                        ,user_id
                    </if>
                </when>
                <otherwise>
                    *
                </otherwise>
            </choose>
        </trim>
    </sql>
    
    <select id="query" resultMap="BaseResultMap">
        <include refid="Select_Column_List"/>
        from team
        <include refid="whereCondition"/>
        <if test="so.pageSize == null or 0 > so.pageSize or so.pageSize > 1000 ">
            LIMIT 200
        </if>
        <if test="so.pageSize != null and so.pageSize > 0 and 1000>=so.pageSize">
            LIMIT #{pageSize}
        </if>
        <if test="so.page != null and so.page > 1">
            OFFSET #{offSet}
        </if>
    </select>
```
