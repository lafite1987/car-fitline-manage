var notices = {
    sunNum : 0,
    editor: null,
    pickerLoaded: true,
    sourceArray: [],
    textCon:null,
    init: function () {
        this.bindEvents();
        this.loadTable();
        this.initEditor();
    },
    //加载表格数据
    loadTable: function () {
        var self = this;
        util.datePickerInit("receiveStartTime", "minDate", "receiveEndTime", "+6m");
        util.datePickerInit("receiveEndTime", "maxDate", "receiveStartTime", "+6m");
        $("#receiveStartTime").bind("copy cut paste keypress", function (e) {
            return false;
        });
        $("#receiveEndTime").bind("copy cut paste keypress", function (e) {
            return false;
        });
        $('input[name=searchInlineRadioOptions]').change(function() {
        	var v = $(this).attr('value');
            if(v == '1') {
                $('#gameName').attr('disabled','disabled');
                $('#gameName').val('所有游戏 (0)');
                $('#appId').val('0');
                $('#noticeType').val('');
            } else if(v == '0'){
                $('#gameName').removeAttr('disabled');
                $('#gameName').val('');
                $('#appId').val('');
                $('#noticeType').val('');
            } else {
            	$('#gameName').removeAttr('disabled');
                $('#gameName').val('');
                $('#appId').val('');
                $('#noticeType').val('1');
            }
         });
        self.query(true);
    },
    load: function (action, argument, refresh) {
        var self = this;
        oTable.dataTable(action, argument, self.insertResult, {refresh: refresh, pageSize: PAGE_SIZE});
    },
    insertResult: function (result,currentPage) {
        var arr = [], $count, $opera;
        if (result.code != 200) {
            return;
        }
        var value = result.value;
        for (var i = 0; i < value.length; i++) {
            var status = "",
                statusName = "",
                statusCode = 0;
            if (value[i].status == 1) {
                status = "下线";
                statusName = "上线";
                statusCode = 0;
            } else {
                status = "上线";
                statusName = "下线";
                statusCode = 1;
            }
            var updateTime = new Date(value[i].updateTime);
            value[i].updateTime = updateTime.format("yyyy-MM-dd hh:mm:ss");

            var startTime = new Date(value[i].startTime);
            value[i].startTime = startTime.format("yyyy-MM-dd hh:mm:ss");

            var endTime = new Date(value[i].endTime);
            value[i].endTime = endTime.format("yyyy-MM-dd hh:mm:ss");

            notices.sunNum = (i+1)+(currentPage*10);
            //'<a href="javascript:void(0);" class="operation J_delete" data-toggle="modal" data-target="#myModal" data-value=' + value[i].id + '>删除</a>
            $opera = '<a href="javascript:;" class="operation J_status" data-id=' + value[i].id + ' data-status=' + statusCode + '>' + status + '</a>'+
                '<a href="javascript:void(0);" class="operation J_strategyInfo" data-toggle="modal" data-target="#myModal" data-value=' + value[i].id + '>详情</a>'+
                '<a href="#" class="operation dialog-editor" data-toggle="modal" data-target="#editorDialog"  data-value=' + value[i].id + '>编辑</a></td>';
            arr.push([notices.sunNum,value[i].appId,'<div class="text_l">'+ value[i].appName +'</div>',value[i].id, '<div class="text_l">'+ value[i].title +'</div>', statusName,value[i].startTime,value[i].endTime,value[i].updateTime, $opera])
        }
        self.num++;
        result.draw = self.num;
        result.recordsTotal = result.total;
        result.recordsFiltered = result.total;
        result.data = arr;
    },
    query: function (refresh) {
        var app_id = $("#appId").val(),
            status = $("#search_dropDown-status").attr("value"),
            title = $("#strategyName").val(),
            startTime = $("#receiveStartTime").val(),
            endTime= $("#receiveEndTime").val(), noticeType = $("#noticeType").val();
        if((startTime !="" && endTime=="") || (endTime !="" && startTime=="")){
            asyncbox.alert("开始时间和结束时间都必须填写！","提示");
            return false;
        }
        var action = "/test/list", argument;
            argument = [
                {name: "appId", value: app_id},
                {name: "status", value: status},
                {name: "title", value: title},
                {name: "startTime", value: startTime},
                {name: "endTime", value: endTime},
                {name:"noticeType", value:noticeType}
            ];
        this.load(action, argument, refresh);
    },
    bindEvents: function () {
        var self = this;
        self.selectDatePick();
        $("#receiveStartTime").val(util.dayInterval(new Date(), -30) + ' 00:00:00');
        $("#receiveEndTime").val(util.dayInterval(new Date(), 30) + ' 23:59:59');
        $("#receiveStartTime,#receiveEndTime").on("copy cut paste keypress", function (e) { return false;});
        
        self.dropDown('modify_search_status', 'search_dropDown-status', 'status');
        self.dropDown('modify_search_status1', 'search_dropDown-status1', 'status1');
        //查询
        $("#gameName").focus(function(){
            if($(this).val() == ""){
                $(this).attr("data-id","");
                $("#appId").val("");
            }
        }).blur(function(){
            if($(this).val() == ""){
                $(this).attr("data-id","");
                $("#appId").val("");
            }
        }).typeahead({
            source: function (typeahead, query) {
                $.get('/console/app/getappsbyname?catId1=2&source=0&start=0&length=50&appName=' + query, function (data) {
                	var str = "所有游戏";
                	if(str.indexOf(query, 0) == 0) {
                		var all = {appId:0, appName:"所有游戏"};
                    	data.value.splice(0, 0, all);
                	}
                	var gameList = [], gameId = [];
                    for (var i = 0; i < data.value.length; i++) {
                        gameList.push(data.value[i].appName + " (" + data.value[i].appId + ")");
                        gameId.push(data.value[i].appId);
                    }
                    typeahead.process(gameList);
                    for (var i = 0; i < gameId.length; i++) {
                        typeahead.$menu.find("li:eq("+ i +")").attr("data-id", gameId[i]);
                    }
                });
            },
            onselect:function(text, id){
                $("#appId").val(id);
            }
        });
        $('.J_search').click(function () {
            self.query(true);
        });
        $('#table').delegate(".J_status", "click", function () {
            //改变状态
            var $this = $(this),
                status = $this.attr("data-status"),
                id = $this.attr("data-id");
            $.get("/console/notice/modify_status", {"status": status, "id": id}, function (result) {
                if (result.code == 200) {
                    if (status == 1) {
                        $this.attr("data-status", 0);
                        $this.text("下线");
                    } else {
                        $this.attr("data-status", 1);
                        $this.text("上线");
                    }
                    self.query();
                }
            });
        }).delegate(".J_strategyInfo","click",function(){
            //详情
            $('.modal-body').html($("#infoDialog").tmpl());
            var sure = $('.modal-footer .btn-primary');
            sure.addClass("none");
            $(".J_detail").removeClass("none");
            $('#myModalLabel').text("详情");
            $('.modal-dialog .modal-body').css({'overflow':"auto",'height':''});
            $('.modal-dialog .modal-content').css({'width':'800px'});
            var id = $(this).attr("data-value");
            $.get("/console/notice/detail",{"id":id}, function (result) {
                var data = result.value;
                if (result.code == 200) {
                    self.insertInfo(result);
                }
            });
        }).delegate(".J_delete","click",function(){
            //删除
            $('.modal-body').empty().html("确定要删除吗？");
            $('#myModalLabel').text("删除");
            var sure = $('.modal-footer .btn-primary');
            sure.addClass("none");
            $(".J_delete_sure").removeClass("none");
            $('.modal-dialog .modal-body').css({'overflow':"auto",'height':''});
            $('.modal-dialog .modal-content').css({'width':'auto'});
            var id = $(this).attr("data-value");
            self.deleteSure(id);
        });
        // editor
        $("#table").on("click", ".dialog-editor", function(){
            $('#myModalLabel').text("编辑公告");
            notices.pickerLoaded = false;
            var sure = $('.modal-footer .btn-primary');
            sure.addClass("none");
            $(".J_sure").removeClass("none");
            notices.gameNameFunc();
            $("#updContent").show();
            var appId = $(this).attr("data-value");
            $("#contentAppId").val(appId);
            $.getJSON("/console/notice/detail", {id: appId}, function(data){
                if ( data.code == 200 ) {
                    if (data.value.status == 1) {
                        $('#search_dropDown-status1').attr("value", '1').text("上线");
                    } else {
                        $('#search_dropDown-status1').attr("value",'0').text("下线");
                    }
                    var startTime = new Date(data.value.startTime);
                    data.value.startTime = startTime.format("yyyy-MM-dd hh:mm:ss");

                    var endTime = new Date(data.value.endTime);
                    data.value.endTime = endTime.format("yyyy-MM-dd hh:mm:ss");

                    $("#gameName2").val(data.value.appName + " (" + data.value.appId + ")").attr("disabled", "disabled");
                    if(data.value.appId == 0) {
                    	$("input[type='radio'][name='inlineRadioOptions'][value='1']").prop("checked", true);// 设置
                    } else {
                    	$("input[type='radio'][name='inlineRadioOptions'][value='0']").prop("checked", true);// 设置
                    }
                    $("input[type='radio'][name='inlineRadioOptions']").attr("disabled", "disabled");
                    $("#appId2").val(data.value.appId);
                    $("#strategyTitle").val(data.value.title);
                    $("#strategyId").val(data.value.id);
                    $("#noticeLink").val(data.value.url);
                    $("#startTime1").val(data.value.startTime);
                    $("#endTime1").val(data.value.endTime);
                    var portrait_img = data.value.portraitImg, landscape_img = data.value.landscapeImg;
                    if (portrait_img == "" || portrait_img == undefined) {
                        $(".vertical .modify_icon").hide();
                        $("#uploadPortrait").html("").show();
                    } else {
                        $("#uploadPortrait").prevAll().remove();
                        var temp = $("#iconTemplate").tmpl(portrait_img);
                        $('#uploadPortrait').before(temp);
                        $(".icon_img").attr("src", portrait_img);
                        $("#iconM").val(portrait_img);
                        $("#uploadPortrait").hide();
                    }
                    if (landscape_img == "" || landscape_img == undefined) {
                        $(".landscape .modify_icon").hide();
                        $("#uploadLandscape").html("").show();
                    } else {
                        $("#uploadLandscape").prevAll().remove();
                        var temp = $("#iconTemplate1").tmpl(landscape_img);
                        $('#uploadLandscape').before(temp);
                        $(".icon_img1").attr("src", landscape_img);
                        $("#iconM1").val(landscape_img);
                        $("#uploadLandscape").hide();
                    }
                    self.editor.focus();
                    var htm = data.value.content;
                    self.editor.html(htm);
                    $("#strategyTextView").html(htm);
                    //$("input:radio").removeAttr("checked");
                    $("input[name='editorShow'][value="+ ((data.value && data.value.show) || 0) +"]").prop("checked", "checked");
                    $('#startTime1').datetimepicker({
                        timeFormat: "HH:mm:ss",
                        dateFormat: "yy-mm-dd",
                        showSecond: true,
                        minDateTime: new Date()
                    });
                    $('#endTime1').datetimepicker({
                        timeFormat: "HH:mm:ss",
                        dateFormat: "yy-mm-dd",
                        showSecond: true,
                        minDateTime: new Date()
                    });
                    $("#editorDialog").delegate(".J_del_img", "click", function () {
                        $(this).parents("td").find(".browse_file").show();
                        $(this).parent().remove();
                    });
                   util.uploadFile('uploadPortrait', self.completeIconImg);
                   util.uploadFile('uploadLandscape', self.completeIconScape);

                   // $("#uploadFile").parents("tr").hide();
                    $("#receiveStartTime").bind("copy cut paste keypress", function (e) {
                        return false;
                    });
                    $("#receiveEndTime").bind("copy cut paste keypress", function (e) {
                        return false;
                    });
                }
                $('#showPreview').mouseenter(function(){
                    notices.textCon = self.editor.html();
                    $("#showPreview").newWindow({
                        windowTitle:"公告内容预览",
                        content: notices.textCon,
                        width:320,height:480
                    });
                });
            });
        });
        $("#updContent").click(function(){
            var portraitImg = $(".vertical .modify_icon .icon_img").attr("src"),
                landscapeImg = $(".landscape .modify_icon .icon_img1").attr("src");
            if (portraitImg == "" || portraitImg == undefined) {
                portraitImg="";
            }
            if (landscapeImg == "" || landscapeImg == undefined) {
                landscapeImg ="";
            }
            var param = {
                id: $("#strategyId").val(),
                appId: $("#appId2").val(),
                title: $("#strategyTitle").val(),
                url: $("#noticeLink").val(),
                content: self.editor.html(),
                status:$("#search_dropDown-status1").attr("value"),
                startTime:$("#startTime1").val(),
                endTime:$("#endTime1").val(),
                portraitImg:portraitImg,
                landscapeImg:landscapeImg
            };
            $.post("/console/notice/modify", param, function(data){
                if ( data.code == 200 ) {
                    self.query();
                    $(".btn-default").trigger("click");
                } else {
                    asyncbox.alert(data.message,"提示");
                }
            });

        });
        //新建
        $(".add_dialog").click(function () {
            notices.pickerLoaded = false;
            $('#editorModal .modal-title').text("新建公告");
            $("#gameName2").removeAttr("disabled");
            var sure = $('.modal-footer .btn-primary');
            sure.addClass("none");
            $(".J_add_sure").removeClass("none");
            //新增
            self.clearData();
            $("input[type='radio'][name='inlineRadioOptions'][value='0']").attr("checked", true);// 设置
            notices.gameNameFunc();
            $('#startTime1').datetimepicker({
                timeFormat: "HH:mm:ss",
                dateFormat: "yy-mm-dd",
                showSecond: true,
                minDateTime: new Date()
            });
            $('#endTime1').datetimepicker({
                timeFormat: "HH:mm:ss",
                dateFormat: "yy-mm-dd",
                showSecond: true,
                minDateTime: new Date()
            });
            $(".modify_icon").remove();
            $("#uploadPortrait").html("").show();
            $("#uploadLandscape").html("").show();
            util.uploadFile('uploadPortrait', self.completeIconImg);
            util.uploadFile('uploadLandscape', self.completeIconScape);
            $("#receiveStartTime").bind("copy cut paste keypress", function (e) {
                return false;
            });
            $("#receiveEndTime").bind("copy cut paste keypress", function (e) {
                return false;
            });
            $('#showPreview').mouseenter(function(){
                notices.textCon = self.editor.html();
                $("#showPreview").newWindow({
                    windowTitle:"公告内容预览",
                    content: notices.textCon,
                    width:320,height:480
                });
            });
            $('input[name=inlineRadioOptions]').change(function(){
                if($(this).attr('value') == '1'){
                    $('#gameName2').attr('disabled','disabled');
                    $('#gameName2').val('所有游戏 (0)');
                    $('#appId2').val('0');
                }else{
                    $('#gameName2').removeAttr('disabled');
                    $('#gameName2').val('');
                    $('#appId2').val('');
                }
             });
            
            self.addSure();
        });
        $("#editorDialog").click(function(evt){
            $('#editorModal .modal-title').text("编辑公告");
            if ( notices.pickerLoaded ) {return;}
            if ( evt.target.id != "editorDialog" && (!$(evt.target).hasClass("btn-default") || $(evt.target).hasClass("btn-default-disable")) ) { return; }
            notices.selectDatePick();
        }).keydown(function(evt){
            if ( evt.which == 27 ) {
                if ( notices.pickerLoaded ) {return;}
                notices.selectDatePick();
            }
        });
        $("#editorDialog").delegate(".J_del_img", "click", function () {
            $(this).parents("td").find(".browse_file").show();
            $(this).parent().remove();
        });

        $("#editorDialog .close,#editorDialog .J_close_sure").click(function(){
            //location.reload();
        });
    },
    selectDatePick: function(){
    	$('#receiveStartTime').datetimepicker({
    		timeFormat: "HH:mm:ss",
    		dateFormat: "yy-mm-dd",
//    	            minDate: util.dayInterval(new Date(), 1),
    		showSecond: true/*,
    	            minDateTime: 'endTimeS'*/
    	});
    	$('#receiveEndTime').datetimepicker({
    		timeFormat: "HH:mm:ss",
    		dateFormat: "yy-mm-dd",
//    	            minDate: util.dayInterval(new Date(), 1),
    		showSecond: true/*,
    	            maxDateTime: 'startTimeS'*/
    	});
        util.datePickerInit("receiveStartTime", "minDate", "receiveEndTime", "+6m");
        util.datePickerInit("receiveEndTime", "maxDate", "receiveStartTime", "+6m");
        notices.pickerLoaded = true;
    },
    insertInfo: function (result) {
        var data = result.value,statusText;
        $(".gameIdText").text(data.appId);
        $(".gameNameText").text(data.appName);
        $(".strategyId").text(data.id);
        $(".strategyIdText").text(data.title);
        if (data.status == 0) {
            statusText = "下线";
        } else {
            statusText = "上线";
        }
        $(".statusText").text(statusText);
        var startTime = new Date(data.startTime);
        data.startTime = startTime.format("yyyy-MM-dd hh:mm:ss");
        $(".startTimeText").text(data.startTime);
        var endTime = new Date(data.endTime);
        data.endTime = endTime.format("yyyy-MM-dd hh:mm:ss");
        $(".endTimeText").text(data.endTime);
        var update_time = new Date(data.updateTime);
        data.updateTime = update_time.format("yyyy-MM-dd hh:mm:ss");
        $(".updateTime").text(data.updateTime);
        $(".contentText").html(data.content);
        $('.urlText').text(data.url).attr('href',data.url);
        $(".landscapeText").attr("src",data.landscapeImg);
        $(".verticalText").attr("src",data.portraitImg);
    },
    addSure: function () {
    	$("input[type='radio'][name='inlineRadioOptions'][value='0']").attr('checked', 'checked');// 设置
        var self = this;
        $(".J_add_sure").unbind('click').click(function () {
        	if($('#appId2').val() == '0') {
        		asyncbox.confirm("确认新增全局公告吗？","全局公告确认",function(result) {
            		if(!(result == 'cancel' || result == '取消')) {
            			self.confirmSubmit();
                	}
            	});
        	} else {
        		self.confirmSubmit();
        	}
        });
    },
    confirmSubmit:function(){
    	var self = this;
    	var param = {
                appId: $("#appId2").val(),
                url: $("#noticeLink").val(),
                title: $("#strategyTitle").val(),
                content: self.editor.html(),
                status:$("#search_dropDown-status1").attr("value"),
                startTime:$("#startTime1").val(),
                endTime:$("#endTime1").val(),
                portraitImg:$(".vertical .modify_icon .icon_img").attr("src"),
                landscapeImg:$(".landscape .modify_icon .icon_img1").attr("src")
            };
            $.post("/console/notice/create", param, function(data){
                if ( data.code == 200 ) {
                    self.query();
                    $(".btn-default").trigger("click");
                } else {
                    asyncbox.alert(data.message,"提示");
                }
            });
    },
    deleteSure:function(verId){
        var self =this;
        $(".J_delete_sure").unbind('click');
        $(".J_delete_sure").click(function () {
            $.get("/console/notice/delete",{"id":verId},function(result){
                if (result.code == 200) {
                    $('#myModal').modal('hide');
                    self.query(true);
                } else {
                    asyncbox.alert("删除失败！\n"+result.message,"提示");
                    $('#myModal').modal('hide');
                }
            });
        });
    },
    dropDown: function (id, text, inp) {
        $('.' + id).delegate('li a', 'click', function () {
            $("#" + text).text($(this).text());
            var value = $(this).attr('value');
            $("input[name =" + inp + "]").attr("value", value);
            $("#" + text).attr("value", value);
        });
    },
    initEditor: function(){
        var _this = this;
        KindEditor.ready(function(K) {
            _this.editor = K.create('textarea[name="content"]', {
                resizeType : 1,
                allowPreviewEmoticons : false,
                allowImageUpload : false,
                items : [
                    'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold', 'italic', 'underline',
                    'removeformat', '|', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist',
                    'insertunorderedlist', '|', 'emoticons'],
                width: '745px',
                height: '400px',
                minWidth: '745px'
            });

            $("span[data-name='emoticons']").after("<span class='ke-outline' " +
                "unselectable='on' title='上传图片'></span>");
            $(".ke-toolbar").height('25px');
        });

        $("#editorModal").width(800);
    },
    gameNameFunc:function(){
        $('#gameName2').focus(function(){
            if($(this).val() == ""){
                $(this).attr("data-id","");
//                $("#appId").val("");
                $("#appId2").val("");
            }
        }).blur(function(){
            if($(this).val() == ""){
                $(this).attr("data-id","");
//                $("#appId").val("");
                $("#appId2").val("");
            }
        }).typeahead({
            source: function (typeahead, query) {
                $.get('/console/app/getappsbyname?catId1=2&source=0&start=0&length=50&appName=' + query, function (data) {
                	var str = "所有游戏";
                	if(str.indexOf(query, 0) == 0) {
                		var all = {appId:0, appName:"所有游戏"};
                    	data.value.splice(0, 0, all);
                	}
                	var gameList = [], gameId = [];
                    for (var i = 0; i < data.value.length; i++) {
                        gameList.push(data.value[i].appName + " (" + data.value[i].appId + ")");
                        gameId.push(data.value[i].appId);
                    }
                    typeahead.process(gameList);
                    for (var i = 0; i < gameId.length; i++) {
                        typeahead.$menu.find("li:eq("+ i +")").attr("data-id", gameId[i]);
                    }
                });
            },
            onselect:function(text, id){
                $("#appId2").val(id);
            }
        });
    },
    clearData:function(){
        $('#search_dropDown-status1').attr("value", '0').text("下线");
        $("#gameName2").val("");
        $("#appId2").val("");
        $("#strategyTitle").val("");
        $("#strategyId").val("");
        this.dropDown('modify_search_status1', 'search_dropDown-status1', 'status1');
        this.editor.focus();
        this.editor.html("");
        $(".window-content").html("");
        $("input[type='radio'][name='inlineRadioOptions'][value='0']").prop('checked', true);// 设置
        $("input[name='editorShow'][value="+ (("") || 0) +"]").prop("checked", "checked");
        $("#noticeLink").val("");
        $("#startTime1").val("");
        $("#endTime1").val("");
    },
    completeIconImg: function (data) {
        var json = jQuery.parseJSON(data);
        var imgUrl = '/upload/' + json.value[0].url;
        var Img = new Image();
        Img.src = imgUrl;
        Img.onload = function () {
            var _width = Img.width, _height = Img.height;
                if (json.code == 200) {
                    var tData = [
                        {
                            url: '/upload/' + json.value[0].url
                        }
                    ];
                    var temp = $("#iconTemplate").tmpl(tData);
                    $('#uploadPortrait').before(temp);
                    $("#uploadPortrait").hide();
                    $("#icon_name").text("");
                } else {
                    asyncbox.alert("上传失败，请重试","提示");
                }
        }
    },
    
    completeIconScape:function(data){
        var json = jQuery.parseJSON(data);
        var imgUrl = '/upload/' + json.value[0].url;
        var Img = new Image();
        Img.src = imgUrl;
        Img.onload = function () {
            var _width = Img.width, _height = Img.height;
                if (json.code == 200) {
                    var tData = [
                        {
                            url: '/upload/' + json.value[0].url
                        }
                    ];
                    var temp = $("#iconTemplate1").tmpl(tData);
                    $('#uploadLandscape').before(temp);
                    $("#uploadLandscape").hide();
                    $("#icon_name1").text("");
                } else {
                    asyncbox.alert("上传失败，请重试","提示");
                }
        }
    }
};
$(function () {
    notices.init();
});


