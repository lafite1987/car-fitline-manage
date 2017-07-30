var users = {
    sunNum : 0,
    editor: null,
    pickerLoaded: true,
    sourceArray: [],
    textCon:null,
    init: function () {
        this.bindEvents();
        this.loadTable();
    },
    //加载表格数据
    loadTable: function () {
        var self = this;
        self.query(true);
    },
    load: function (action, argument, refresh) {
        var self = this;
        oTable.dataTable(action, argument, self.insertResult, {refresh: refresh, pageSize: PAGE_SIZE});
    },
    insertResult: function (result,currentPage) {
        var arr = [], $opera;
        if (result.code != 200) {
            return;
        }
        var value = result.value;
        for (var i = 0; i < value.length; i++) {
            var createTime = new Date(value[i].createTime);
            value[i].createTime = createTime.format("yyyy-MM-dd hh:mm:ss");

            users.sunNum = (i+1)+(currentPage*PAGE_SIZE);
            $opera = '<a href="#" class="operation J_delete" data-toggle="modal" data-target="#myModal" data-value=' + value[i].id + '>删除</a>' + 
            	'<a href="#" class="operation J_strategyInfo" data-toggle="modal" data-target="#myModal" data-value=' + value[i].id + '>详情</a>'+
                '<a href="#" class="operation dialog-editor" data-toggle="modal" data-target="#editorDialog"  data-value=' + value[i].id + '>编辑</a>' + 
                '<a href="#" class="operation dialog-roles" data-toggle="modal" data-target="#rolesDialog"  data-value=' + value[i].id + '>分配角色</a>' +
                '</td>';
            arr.push([users.sunNum,value[i].id,value[i].username,value[i].nickname, '<div class="text_l">'+ value[i].phone +'</div>', value[i].state,value[i].createTime, $opera]);
        }
        self.num++;
        result.draw = self.num;
        result.recordsTotal = result.total;
        result.recordsFiltered = result.total;
        result.data = arr;
    },
    query: function (refresh) {
        var action = "/manage/user/api/list", argument;
            argument = [
                {test:"test"}
            ];
        this.load(action, argument, refresh);
    },
    dropDown: function (id, text, inp) {
        $('.' + id).delegate('li a', 'click', function () {
            $("#" + text).text($(this).text());
            var value = $(this).attr('value');
            $("input[name =" + inp + "]").attr("value", value);
            $("#" + text).attr("value", value);
        });
    },
    bindEvents: function () {
        var self = this;
        self.dropDown('modify_search_status1', 'search_dropDown-status1', 'status1');
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
            $.get("/manage/user/detail",{"id":id}, function (result) {
                if (result.ret == 0) {
                    self.insertInfo(result);
                }
            });
        }).delegate(".J_delete","click",function(){
        	//删除
            $('.modal-body').empty().html("确定要删除吗？");
            $('#myModalLabel').text("删除用户");
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
            $('#myModalLabel').text("编辑用户");
            users.pickerLoaded = false;
            var sure = $('.modal-footer .btn-primary');
            sure.addClass("none");
            $(".J_sure").removeClass("none");
            $("#updContent").show();
            var id = $(this).attr("data-value");
            self.clearData();
            $.getJSON("/manage/user/detail", {id: id}, function(result){
            	if (result.ret == 0) {
                    if (result.data.state == 1) {
                        $('#search_dropDown-status1').attr("value", '1').text("有效");
                    } else {
                        $('#search_dropDown-status1').attr("value",'0').text("禁止");
                    }
                    $("#id").val(result.data.id);
                    $("#username").val(result.data.username);
                    $("#nickname").val(result.data.nickname);
                    $("#phone").val(result.data.phone);
            	}
            });
        }).on("click", ".dialog-roles", function(){
        	$(".J_roles_sure").removeClass("none");
            var id = $(this).attr("data-value");
            $(".J_roles_sure").unbind().click(function () {
            	var treeObj=$.fn.zTree.getZTreeObj("roleTree");
                var nodes=treeObj.getCheckedNodes(true);
                
                var roleIds = [];
                for(var i = 0; i < nodes.length; i++) {
                	tmpNode = nodes[i];
					if(i!=nodes.length-1){
						roleIds += tmpNode.id+",";
					}else{
						roleIds += tmpNode.id;
					}
                }
                $.get("/manage/user/role/save",{userId:id, roleIds : roleIds},function(result){
                    if (result.ret == 0) {
                        $('#rolesDialog').modal('hide');
                    } else {
                        asyncbox.alert("分配权限失败！\n"+result.msg,"提示");
                        $('#rolesDialog').modal('hide');
                    }
                });
            });
            $.getJSON("/manage/user/role/tree", {id: id}, function(result){
            	if (result.ret == 0) {
            		var setting = {
        				check: {
        	        		chkboxType : { "Y" : "", "N" : "" },
        	        		enable: true
        	        	},
        	            view: {
        	                selectedMulti: false
        	            },
        	            data: {
        	                simpleData: {
        	                    enable: true,
        	                    idKey: "id",
        	    				pIdKey: "parentId",
        	                }
        	            },
        	            callback: {
        	            	
        	            }
        	        };
            		var zNodes = result.data;
        			$.fn.zTree.init($("#roleTree"), setting, zNodes);
        			$('#rolesDialog').modal('show');
            	}
            });
        });
        $("#updContent").click(function(){
        	var password = $("#password").val();
        	if(password) {
        		password = sha256(password);
        	}
            var param = {
                id: $("#id").val(),
                username: $("#username").val(),
                password: password,
                nickname: $("#nickname").val(),
                phone: $("#phone").val(),
                state:$("#search_dropDown-status1").attr("value")
            };
            $.post("/manage/user/update", param, function(result){
                if ( result.ret == 0 ) {
                    self.query();
                    $(".btn-default").trigger("click");
                } else {
                    asyncbox.alert(result.msg,"提示");
                }
            });

        });
        //新建
        $(".add_dialog").click(function () {
        	var sure = $('.modal-footer .btn-primary');
            sure.addClass("none");
            $(".J_add_sure").removeClass("none");
            self.clearData();
        	self.addSure();
        });

        $("#editorDialog .close,#editorDialog .J_close_sure").click(function(){
            //location.reload();
        });
    },
    insertInfo: function (result) {
    	var data = result.data;
    	$(".idText").text(data.id);
        $(".usernameText").text(data.username);
        $(".nicknameText").text(data.nickname);
        $(".phoneText").text(data.phone);
        var statusText = "";
        if (data.state == 0) {
            statusText = "禁用";
        } else {
            statusText = "有效";
        }
        $(".stateText").text(statusText);
        var createTime = new Date(data.createTime);
        data.createTime = createTime.format("yyyy-MM-dd hh:mm:ss");
        $(".createTimeText").text(data.createTime);
    },
    addSure: function () {
        var self = this;
        $(".J_add_sure").unbind('click').click(function () {
        	self.confirmSubmit();
        });
    },
    confirmSubmit:function(){
    	var self = this;
    	var param = {
                username: $("#username").val(),
                password: $("#password").val(),
                nickname: $("#nickname").val(),
                phone: $("#phone").val(),
                state:$("#search_dropDown-status1").attr("value")
            };
            $.post("/manage/user/add", param, function(result){
                if ( result.ret == 0 ) {
                    self.query();
                    $(".btn-default").trigger("click");
                } else {
                    asyncbox.alert(result.msg,"提示");
                }
            });
    },
    deleteSure:function(id){
        var self =this;
        $(".J_delete_sure").unbind('click');
        $(".J_delete_sure").click(function () {
            $.get("/manage/user/del",{"id":id},function(result){
                if (result.ret == 0) {
                    $('#myModal').modal('hide');
                    self.query();
                } else {
                    asyncbox.alert("删除失败！\n"+result.msg,"提示");
                    $('#myModal').modal('hide');
                }
            });
        });
        $('#myModal').modal('show');
    },
    dropDown: function (id, text, inp) {
        $('.' + id).delegate('li a', 'click', function () {
            $("#" + text).text($(this).text());
            var value = $(this).attr('value');
            $("input[name =" + inp + "]").attr("value", value);
            $("#" + text).attr("value", value);
        });
    },
    clearData:function(){
    	$("#username").val("");
        $("#password").val("");
        $("#nickname").val("");
        $("#phone").val("");
    },
};
$(function () {
    users.init();
});


