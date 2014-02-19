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
    
    for d in data:
        print d, data[d]
    
    if User.objects.filter(fbId=data.get('fbUserId',0)).exists():
        print "Exists!"
        
    else:
        # add into database with his friends
        newUser = User(
                       fbId = data.get('fbUserId',0),
                       fbName = data.get("fbUserName", "")
                       
                       )
        newUser.save()
    
    
    return HttpResponse("")