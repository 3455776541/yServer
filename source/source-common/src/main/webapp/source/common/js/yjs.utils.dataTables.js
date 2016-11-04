/**
 * yjs.utils.dataTables.js
 * 依赖于:y.utils.core.js、jquery.dataTables.min.js、dataTables.colVis.min.js、dataTables.tableTools.min.js、datatables.responsive.min.js
 *
 * Created by ysj on 2016
 */

!function (a, b, c) {
    c.dataTables = {
        hasHead: true,
        successCall: function (r) {
        },
        errorCall: function (e) {
        }
    };

    /**
     * 初始化表格
     *
     * @param url
     * @param tableObj
     * @param columns
     * @param columnsFormatter
     * @returns {*}
     */
    c.dataTables.onInit = function (url, tableObj, columns, columnsFormatter) {
        if (c.checkEmpty(url) || c.checkEmpty(tableObj)) {
            alert("URLs and table can not be empty.");
            return;
        }
        return tableObj.DataTable(c.dataTables.getDataTableConfig(url, tableObj, columns, columnsFormatter));
    };

    /**
     * getDataTable
     *
     * @param url
     * @param tableObj
     * @param config
     */
    c.dataTables.getDataTable = function (tableObj, config) {
        if (c.checkEmpty(tableObj)) {
            alert("Table can not be empty.");
            return;
        }
        if (c.checkEmpty(config)) {
            alert("Config can not be empty.");
            return;
        }
        return tableObj.DataTable(config);
    };

    c.dataTables.getDataTableConfig = function (url, tableObj, columns, columnsFormatter) {
        if (c.checkEmpty(url) || c.checkEmpty(tableObj)) {
            alert("URLs and table can not be empty.");
            return;
        }
        var responsiveHelper_datatable_column = undefined;
        var breakpointDefinition = {
            tablet: 1024,
            phone: 480
        };
        // 拼装表头信息
        var conditionTr = "<tr>";
        var headTr = "<tr>";
        for (var index in columns) {
            var col = columns[index];
            var hasData = !c.checkEmpty(col.mData);
            var sName = c.checkEmpty(col.sName) ? col.mData : col.sName;
            var sType = c.checkEmpty(col.sType) ? "text" : col.sType;
            var sTitle = c.checkEmpty(col.sTitle) ? (hasData ? col.mData : "") : col.sTitle;
            var cTitle = c.checkEmpty(col.cTitle) ? sTitle : col.cTitle;
            var searchable = col.searchable != false ? true : col.searchable;
            conditionTr += "<th class='hasinput" + (col.class == 'hidden' ? " " + col.class : "") + "'>"
                + (hasData && searchable ? "<input name='" + sName + "' type='" + sType + "' class='form-control custom-condition' placeholder='" + cTitle + "'/>" : "")
                + "</th>";
            headTr += "<th>" + sTitle + "</th>"
        }
        conditionTr += "</tr>";
        headTr += "</tr>";
        if (c.dataTables.hasHead) {
            tableObj[0].innerHTML = "<thead>" + conditionTr + headTr + "</thead>";
        } else {
            tableObj.find('thead').append(headTr);
        }
        var config = {
            "oLanguage": { // 汉化
                "sLengthMenu": "每页显示 _MENU_ 条记录",
                "sZeroRecords": "没有检索到数据",
                "sInfo": "显示 _START_ 至 _END_ 条 &nbsp;&nbsp;共 _TOTAL_ 条",
                "sInfoFiltered": "(筛选自 _MAX_ 条数据)",
                "sInfoEmpty": "显示 0 至 0 条 &nbsp;&nbsp;共 0 条",
                "sProcessing": "正在加载数据...",
                "sProcessing": "<img src='source/smart/img/ajax-loader.gif' />", //这里是给服务器发请求后到等待时间显示的 加载gif
                "oPaginate": {
                    "sFirst": "首页",
                    "sPrevious": "前一页",
                    "sNext": "后一页",
                    "sLast": "尾页"
                }
            },
            "sPaginationType": "full_numbers", // 分页，一共两种样式 ，默认是两个按钮那种
            "bProcessing": true,
            "deferRender": true,//当处理大数据时，延迟渲染数据，有效提高Datatables处理能力
            "bPaginate": true,// 分页按钮 默认为true
            "aoColumns": null != columns ? columns : [],
            "aoColumnDefs": null != columnsFormatter ? columnsFormatter : [],
            "bServerSide": true,
            "sAjaxSource": url,
            "sServerMethod": "POST",
            "fnServerData": function (sSource, aoData, fnCallback, oSettings) {
                var params = c.toMap(aoData);
                var colArray = params["sColumns"].split(",");
                var sortIndex = params["iSortCol_0"];
                var sortMode = params["sSortDir_0"];
                var col = colArray[sortIndex];
                if (undefined != col && null != col && "" != col) {
                    aoData.push({"name": "sortValue", "value": col});
                    aoData.push({"name": "sortMode", "value": sortMode});
                }
                aoData.push({"name": "columns", "value": colArray});
                aoData.push({"name": "page", "value": params["iDisplayStart"] / params["iDisplayLength"]});
                aoData.push({"name": "size", "value": params["iDisplayLength"]});
                //表单栏位
                var conditions = tableObj.find(".custom-condition");
                for (var i = 0; i < conditions.length; i++) {
                    var condition = conditions[i];
                    if (!c.checkEmpty(condition.name) && !c.checkEmpty(condition.value)) {
                        aoData.push({"name": "params[" + condition.name + "]", "value": condition.value});
                    }
                }
                oSettings.jqXHR = a.jQuery.ajax({
                    "dataType": 'json',
                    "type": "POST",
                    "url": sSource,
                    "data": aoData,
                    "success": function (result) {
                        var successCallBack = c.dataTables.successCall;
                        if (null != successCallBack && undefined != successCallBack) successCallBack(result);
                        var data = result.data;
                        if (data instanceof Array) {
                            result.iTotalRecords = data.length;
                            result.iTotalDisplayRecords = data.length;
                        } else {
                            result.iTotalRecords = data.totalElements;
                            result.iTotalDisplayRecords = data.totalElements;
                            result.data = yjs.checkNotEmpty(data.content) ? data.content : [];
                        }
                        fnCallback(result);//服务器端返回的对象resp是要求的格式
                    },
                    "error": function (error) {
                        var errorCallBack = c.dataTables.errorCall;
                        if (null != errorCallBack && undefined != errorCallBack) errorCallBack(error);
                        var result = "系统异常";
                        try {
                            result = error.responseJSON;
                            dialog.error(result.msg);
                        } catch (e) {
                            alert(result);
                        }
                    }
                });
            },
            "sDom": "<'dt-toolbar'<'col-sm-6 col-xs-12 hidden-xs'>r<'col-sm-6 col-xs-12 hidden-xs'l>>" +
            "t" + "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-sm-6 col-xs-12'p>>",
            "autoWidth": true,
            "preDrawCallback": function () {
                // Initialize the responsive datatables helper once.
                if (!responsiveHelper_datatable_column) {
                    responsiveHelper_datatable_column = new ResponsiveDatatablesHelper(tableObj, breakpointDefinition);
                }
            },
            "rowCallback": function (nRow) {
                responsiveHelper_datatable_column.createExpandIcon(nRow);
            },
            "drawCallback": function (oSettings) {
                responsiveHelper_datatable_column.respond();
            }
        }
        return config;
    };
}(window, document, yjs);