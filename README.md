项目介绍
这是一款基于安卓的校园线上诊疗项目
    该项目设计的广义初衷是为了简化校内学生问诊流程，
尤其是是当距离校医院比较远（比如我们学校就是）且身体不舒服的时候不愿意走动的情况下即可使用我们的app与校医院端进行线上挂号，问诊，开药等流程。
在开发过程中19年底时刚好遇到武汉疫情爆发，这使得线上问诊可以避免线下引发群聚感染的优势更加突出明显。
    设计该项目的狭义初衷是希望通过所学知识（大二结束时的暑假开始的大概，主要专业课如数据库，java，web等课程已经学完）和小组合作的方式开发出一款尽可能贴近现实应用场景的一款项目。
然后但是对安卓开发特别感兴趣，所以客户端选择基于java的安卓（当时手机安卓版本用的安卓9，所以就基于安卓9环境，其他版本没有适配，可能会出现部分错误）进行开发的。

主要开发工具:Android Studio（客户端) eclipse（服务器Servlet） Navicat（MySQL数据库） Git(版本控制)

服务器端主要基于Servlet实现，部署在tomcat服务器上（代码详见仓库IM）

主要功能点: 
    利用WebRTC音视频通话技术实现在线视频问诊，通过利用STUN协议进行NAT网络穿透进行P2P通信
以降低中继服务器所带来过高的带宽消耗。
    利用WebSocket长连接实现用户挂号，文字聊天。同时引入心跳检测检测机制增强socket连接的稳定
性。
    服务端使用线程安全的队列来实现多医生多病人的同时接诊问诊。
    接入第三方支付平台：支付宝沙箱环境 。




