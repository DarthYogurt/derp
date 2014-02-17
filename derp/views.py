
import json

from django.http.request import HttpRequest
from django.http.response import HttpResponse
from django.views.decorators.csrf import csrf_exempt

from derp.models import *


@csrf_exempt
def login(request):
    
    dataString = request.FILES.get('data', "empty")
    if dataString == "empty":
        return HttpResponse("Post Data Empty")
    data = json.load(dataString)
    
    print data
    return HttpResponse("Testing Field")

