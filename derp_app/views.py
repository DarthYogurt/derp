# -*- coding: utf-8 -*-
import datetime
import json
import os
import sys

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
    
    #for d in data:  print d,data[d]
    
    if User.objects.filter(fbId=data.get('fbUserId',0)).exists():
        #print "User exists"
        True
        # need to update user info
    else:
        #print "not exist"
        newUser = User(
                       fbId = data.get('fbUserId',0),
                       fbName = data.get("fbUserName", ""),
                       activated = True,                   
                       )
        newUser.save()
    
    
        for friend in data['fbFriends']:
            try:
                #fbName =""
                print friend
                fbName = friend['fbName'] #.encode("utf-8") #.encode("utf-8")   
         
                if User.objects.filter(fbId=friend.get('fbId',0)).exists():
                    #print "already in User Database"
                    #Update this friend again
                    updateUser = User.objects.get(fbId=friend.get('fbId',0))
                    updateUser.fbName = fbName #friend.get("fbName",0)
                    updateUser.save()
                else:
                    newUser = User(
                                   fbId=friend.get("fbId",0),
                                   fbName= fbName, #friend.get("fbName",0),
                                   activated = False
                                   )
                    newUser.save()

            except:
                print "skipped"
        
    return HttpResponse("")



@csrf_exempt
def uploadPic(request):
    dataString = request.FILES.get('data', "empty")
    if dataString == "empty":
        return HttpResponse("Post Data Empty")
    data = json.load(dataString)
    
    for d in data:
        print d,data[d]
    
    print request.FILES['image']
    
    
    newPicture = Picture(
                         poster = User.objects.get(fbId=data.get("fbUserId",1)),
                         targetFbId = User.objects.get(fbId=data.get("targetFbId", 1)),
                         image = request.FILES['image'],
                         caption = data.get("caption", 0)
                         )
    newPicture.save()
    return HttpResponse("")

def getPic(request, picId):
    
#     
#     checklists = List.objects.filter(group=groupId)
#     j = {}
#     j['groupId'] = int(groupId)
#     j['checklist'] = []
#     
#     if len(checklists) == 0:
#         j['error'] = "No Results"
#         return HttpResponse(json.dumps(j), content_type="application/json")
#     for item in checklists:
#         temp = {}
#         temp['name'] = item.name
#         temp['numOfSteps'] = ListStep.objects.filter(list=List.objects.get(id=item.id)).count()
#         temp['id'] = item.id
#         
#         j['checklist'].append(temp)
#     return HttpResponse(json.dumps(j), content_type="application/json")
    return HttpResponse(picId)

def getTeamGallery(request,userId):
    
    
    return HttpResponse(userId)
    
    
    


@csrf_exempt
def addComment(request):
    dataString = request.FILES.get('data', "empty")
    if dataString == "empty":
        return HttpResponse("Post Data Empty")
    data = json.load(dataString)
    
    for d in data:
        print d,data[d]

    newComment = Comment(
                         picture = Picture.objects.get(id = data.get("pictureId", 1)),
                         poster = User.objects.get(id = data.get("posterId",1)),
                         comment = data.get("comment",""),
                         timeModified = datetime.datetime.today()
                         
                         )

    return HttpResponse("done")



@csrf_exempt
def uploadError(request):
    f = open("error.html", "w")
    f.write(request.FILES['error'].read())
    f.close()
    return HttpResponse("")

def latestError(request):
    f = open( os.getcwd() + "/error.html", "rb")
    #f = open( "E:\\coding_workspace\\medusa_backend\\tempJson", "rb")
    stringReturn = f.read()
    return HttpResponse(stringReturn)
    