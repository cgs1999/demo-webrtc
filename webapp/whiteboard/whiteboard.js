var WebsocketPath = "/whiteboard.do";

/* BrowserDetect came from http://www.quirksmode.org/js/detect.html */
var BrowserDetect = {
	init : function() {
		this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
		this.version = this.searchVersion(navigator.userAgent) || this.searchVersion(navigator.appVersion)
				|| "an unknown version";
		this.OS = this.searchString(this.dataOS) || "an unknown OS";
	},
	searchString : function(data) {
		for ( var i = 0; i < data.length; i++) {
			var dataString = data[i].string;
			var dataProp = data[i].prop;
			this.versionSearchString = data[i].versionSearch || data[i].identity;
			if (dataString) {
				if (dataString.indexOf(data[i].subString) != -1)
					return data[i].identity;
			} else if (dataProp)
				return data[i].identity;
		}
	},
	searchVersion : function(dataString) {
		var index = dataString.indexOf(this.versionSearchString);
		if (index == -1)
			return;
		return parseFloat(dataString.substring(index + this.versionSearchString.length + 1));
	},
	dataBrowser : [ {
		string : navigator.userAgent,
		subString : "Chrome",
		identity : "Chrome"
	}, {
		string : navigator.userAgent,
		subString : "OmniWeb",
		versionSearch : "OmniWeb/",
		identity : "OmniWeb"
	}, {
		string : navigator.vendor,
		subString : "Apple",
		identity : "Safari",
		versionSearch : "Version"
	}, {
		prop : window.opera,
		identity : "Opera",
		versionSearch : "Version"
	}, {
		string : navigator.vendor,
		subString : "iCab",
		identity : "iCab"
	}, {
		string : navigator.vendor,
		subString : "KDE",
		identity : "Konqueror"
	}, {
		string : navigator.userAgent,
		subString : "Firefox",
		identity : "Firefox"
	}, {
		string : navigator.vendor,
		subString : "Camino",
		identity : "Camino"
	}, { // for newer Netscapes (6+)
		string : navigator.userAgent,
		subString : "Netscape",
		identity : "Netscape"
	}, {
		string : navigator.userAgent,
		subString : "MSIE",
		identity : "Explorer",
		versionSearch : "MSIE"
	}, {
		string : navigator.userAgent,
		subString : "Gecko",
		identity : "Mozilla",
		versionSearch : "rv"
	}, { // for older Netscapes (4-)
		string : navigator.userAgent,
		subString : "Mozilla",
		identity : "Netscape",
		versionSearch : "Mozilla"
	} ],
	dataOS : [ {
		string : navigator.platform,
		subString : "Win",
		identity : "Windows"
	}, {
		string : navigator.platform,
		subString : "Mac",
		identity : "Mac"
	}, {
		string : navigator.userAgent,
		subString : "iPhone",
		identity : "iPhone/iPod"
	}, {
		string : navigator.platform,
		subString : "Linux",
		identity : "Linux"
	} ]

};
BrowserDetect.init();

var pos = 0;

function get_appropriate_ws_url() {
	var pcol;
	var u = document.URL;

	/*
	 * We open the websocket encrypted if this page came on an
	 * https:// url itself, otherwise unencrypted
	 */

	if (u.substring(0, 5) == "https") {
		pcol = "wss://";
		u = u.substr(8);
	} else {
		pcol = "ws://";
		if (u.substring(0, 4) == "http")
			u = u.substr(7);
	}

	u = u.split('/');

	return pcol + u[0];
}

/* lws-mirror protocol */

var down = 0;
var no_last = 1;
var last_x = 0, last_y = 0;
var ctx;
var socket_lm;
var color = "#000000";

if (BrowserDetect.browser == "Firefox") {
	socket_lm = new MozWebSocket(get_appropriate_ws_url() + WebsocketPath, "lws-mirror-protocol");
} else {
//	socket_lm = new WebSocket(get_appropriate_ws_url() + WebsocketPath, "lws-mirror-protocol");
	socket_lm = new WebSocket(get_appropriate_ws_url() + WebsocketPath);
}

try {
	socket_lm.onopen = function() {
		document.getElementById("wslm_statustd").style.backgroundColor = "#40ff40";
		document.getElementById("wslm_status").textContent = " websocket connection opened ";
	}

	socket_lm.onmessage = function got_packet(msg) {
		j = msg.data.split(';');
		f = 0;
		while (f < j.length - 1) {
			i = j[f].split(' ');
			if (i[0] == 'd') {
				ctx.strokeStyle = i[1];
				ctx.beginPath();
				ctx.moveTo(+(i[2]), +(i[3]));
				ctx.lineTo(+(i[4]), +(i[5]));
				ctx.stroke();
			}
			if (i[0] == 'c') {
				ctx.strokeStyle = i[1];
				ctx.beginPath();
				ctx.arc(+(i[2]), +(i[3]), +(i[4]), 0, Math.PI * 2, true);
				ctx.stroke();
			}

			f++;
		}
	}

	socket_lm.onclose = function() {
		document.getElementById("wslm_statustd").style.backgroundColor = "#ff4040";
		document.getElementById("wslm_status").textContent = " websocket connection CLOSED ";
	}
} catch (exception) {
	alert('<p>Error' + exception);
}

var canvas = document.createElement('canvas');
canvas.height = 480;
canvas.width = 640;
ctx = canvas.getContext("2d");

document.getElementById('wslm_drawing').appendChild(canvas);

canvas.addEventListener('mousemove', ev_mousemove, false);
canvas.addEventListener('mousedown', ev_mousedown, false);
canvas.addEventListener('mouseup', ev_mouseup, false);

offsetX = offsetY = 0;
element = canvas;
if (element.offsetParent) {
	do {
		offsetX += element.offsetLeft;
		offsetY += element.offsetTop;
	} while ((element = element.offsetParent));
}

function update_color() {
	color = document.getElementById("color").value;
}

function ev_mousedown(ev) {
	down = 1;
}

function ev_mouseup(ev) {
	down = 0;
	no_last = 1;
}

function ev_mousemove(ev) {
	var x, y;

	if (ev.offsetX) {
		x = ev.offsetX;
		y = ev.offsetY;
	} else {
		x = ev.layerX - offsetX;
		y = ev.layerY - offsetY;

	}

	if (!down)
		return;
	if (no_last) {
		no_last = 0;
		last_x = x;
		last_y = y;
		return;
	}
	socket_lm.send("d " + color + " " + last_x + " " + last_y + " " + x + ' ' + y + ';');

	last_x = x;
	last_y = y;
}