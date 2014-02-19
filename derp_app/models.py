from django.db import models

# Create your models here.
class User(models.Model):
    name = models.CharField(max_length=50)
    phone = models.CharField(max_length=20)
    email = models.CharField(max_length=15)
    
    fbId = models.IntegerField()
    fbName = models.CharField()
    
    def __unicode__(self):
        return self.name + "-"+str(self.id)