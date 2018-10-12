/*!
 *@name     jquery.barrager.js
 *@author   yaseng@uauc.net
 *@url      https://github.com/yaseng/jquery.barrager.js
 */
(function($) {

	$.fn.barrager = function(barrage) {
		barrage = $.extend({
			close:true,
			bottom: 0,
			max: 10,
			speed: 6,
			color: '#fff',
			old_ie_color : '#000000'
		}, barrage || {});
		var time = new Date().getTime();
		var barrager_id = 'barrage_' + time;
		var id = '#' + barrager_id;
		var div_barrager = $("<div class='barrage' id='" + barrager_id + "'></div>").appendTo($(this));
		
		var window_height = $(window).height() - 400;
		var window_width = $(window).width() * 0.7;

		var bottom = (barrage.bottom == 0) ? Math.floor(Math.random() * window_height + 200) : barrage.bottom;
		div_barrager.css("bottom", bottom + "px");
	
		div_barrager_box = $("<div class='barrage_box cl'></div>").appendTo(div_barrager);
		if(barrage.img){

			div_barrager_box.append("<a class='portrait z' href='javascript:;'></a>");
			var img = $("<img src='' >").appendTo(id + " .barrage_box .portrait");
			img.attr('src', barrage.img);
		}
		
		div_barrager_box.append(" <div class='z p'></div>");
		if(barrage.close){

			div_barrager_box.append(" <div class='close z'></div>");

		}
		
		var content = $("<a title='' href='' target='_blank'></a>").appendTo(id + " .barrage_box .p");
		content.attr({
			'href': barrage.href,
			'id': barrage.id
		}).empty().append(barrage.info);
		if(navigator.userAgent.indexOf("MSIE 6.0")>0  ||  navigator.userAgent.indexOf("MSIE 7.0")>0 ||  navigator.userAgent.indexOf("MSIE 8.0")>0  ){

			content.css('color', barrage.old_ie_color);

		}else{

			content.css('color', barrage.color);

		}

		// 获取弹幕宽度,设置width，right
		var div_barrager_width = div_barrager.css("width");
		var j = -parseInt(div_barrager_width) + parseInt($(window).width() * 0.08);
		div_barrager.css("width", div_barrager_width);
		div_barrager.css("right", j);

		var distance = $(window).width() * 0.7 + -j;

		//显示记录
		setTimeout(function() {
				chatMessage(barrage.info);
		},6000);
		// 记录框
		function chatMessage(message) {
			
			var randomAvatar = parseInt(Math.random() * 3) + 1;

			var liDom = "<li class=\"danmakuUser\"><p id=\"liDomTxt\"><span><img src=\"img/avatar" + randomAvatar + ".svg\"></span>&nbsp;&nbsp;:&nbsp;&nbsp;" + message + "</p></li>";
			// var liDom = "<li class=\"danmakuUser\"><p id=\"liDomTxt\"><span><img src=\"img/avatar" + randomAvatar + ".svg\"></span>&nbsp;&nbsp;:&nbsp;&nbsp;</p></li>";
			console.log("111");
		    $(".ulDanmaku").append(liDom);
		    
		    var i = 0;
		    
		    // var show = function() {
		    // 	var str = '';
		    // 	str = message.substr(0, i);
		    // 	$("#liDomTxt").html(str);
		    // 	i++;
		    // 	if (i > message.length) {
		    // 		i = 0;
		    // 	}
		    // 	setTimeout("show()", 200);
		    // }
		    // show();
		    //滚动条始终在底部
		    var div = document.getElementById('danmakuInnerBox');
		    div.scrollTop = div.scrollHeight;
		}

		
		var i = 0;
		div_barrager.css('margin-right', 0);
		
 		$(id).animate({right:window_width},barrage.speed*1000,"linear",function(){

			$(id).remove();
		});

		div_barrager_box.mouseover(function() {
		     $(id).stop(true);
		});

		div_barrager_box.mouseout(function() {

			$(id).animate({right:window_width},barrage.speed*1000,function(){

				$(id).remove();
			});

 		});

		$(id+'.barrage .barrage_box .close').click(function(){

			$(id).remove();

		})

		
	}
	

	$.fn.barrager.removeAll=function(){

		 $('.barrage').remove();

	}

})(jQuery);