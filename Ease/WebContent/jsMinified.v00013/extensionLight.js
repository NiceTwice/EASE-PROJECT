var extensionLight={runtime:{sendMessage:function(e,s,a){safari.self.tab.dispatchMessage(e,s),safari.self.addEventListener("message",function n(s){s.name==e+" response"&&(safari.self.removeEventListener(n),a(s.message))},!1)},onMessage:function(e,s){safari.self.addEventListener("message",function(a){function n(e){safari.self.tab.dispatchMessage(a.name+" response from tab "+a.message.tab,e)}a.name==e&&s(a.message.msg,n)},!1)}}};