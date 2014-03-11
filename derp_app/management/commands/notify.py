 
'''
Created on Mar 11, 2014

@author: cyrano821
Notification adding
'''

# from django.core.management.base import BaseCommand, CommandError
# 
# 
# class Command(BaseCommand):
#     
#     a = 0
#     
# if __name__ == '__main__':
#     pass

from django.core.management.base import BaseCommand, CommandError


class Command(BaseCommand):
    args = '<poll_id poll_id ...>'
    help = 'Closes the specified poll for voting'

    def handle(self, *args, **options):
#         for poll_id in args:
#             try:
#                 poll = Poll.objects.get(pk=int(poll_id))
#             except Poll.DoesNotExist:
#                 raise CommandError('Poll "%s" does not exist' % poll_id)
# 
#             poll.opened = False
#             poll.save()
     
        self.stdout.write("testing complete")
