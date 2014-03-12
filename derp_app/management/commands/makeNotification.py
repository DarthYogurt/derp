'''
Created on Mar 11, 2014

@author: cyrano821
Notification adding
'''

from django.core.management.base import BaseCommand, CommandError
from derp_app.models import *
import datetime

class Command(BaseCommand):

    def handle(self, *args, **options):
    	for user in User.objects.filter(activated=True):
    	 	postedToday = Picture.objects.filter(posterId = user, date__gte=datetime.datetime.now().date()).exists()	
    	 	if not postedToday:
    	 		if not Notification.objects.filter(targetUser=user).exists():
	    	 		newNotification = Notification(
	    	 							targetUser = user,
	    	 							type="reminder",
	    	 							text="Have you Derped someone today?",
	    	 							date=datetime.datetime.now())
	    	 		newNotification.save()