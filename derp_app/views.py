import json

from django.http.response import HttpResponse
from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt

from derp_app.models import *


# Create your views here.
@csrf_exempt
def login(request):
    dataString = request.FILES.get('data', "empty")
    if dataString == "empty":
        return HttpResponse("Post Data Empty")
    data = json.load(dataString)
    
    if User.objects.filter(fbId=data.get('fbUserId',0)).exists():
        print "Exists!"
        #need to update friends list
    else:
        newUser = User(
                       fbId = data.get('fbUserId',0),
                       fbName = data.get("fbUserName", "")                   
                       )
        newUser.save()
    
    for friend in data['fbFriends']:
        if Friend.objects.filter(fbId=friend.get("fbId", 0)).exists():
            updateFriend = Friend.objects.get(fbId = friend.get('fbId',0), parentFriend = User.objects.get(fbId= data.get('fbUserId',0)))
            updateFriend.name = friend.get("fbName","")
            updateFriend.save()
        else:
            newFriend = Friend(
                               parentFriend = User.objects.get(fbId=data.get("fbUserId",0)),
                               fbId = friend.get("fbId",0),
                               name = friend.get("fbName","")
                               )
            newFriend.save()
    
    return HttpResponse("")