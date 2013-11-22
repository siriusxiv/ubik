#!/usr/bin/perl

use Socket;

my $proto = getprotobyname('tcp');
socket(Server,PF_INET,SOCK_STREAM,$proto) || die "socket: $!";
setsockopt(Server,SOL_SOCKET,SO_REUSEADDR,pack("l",1)) || die "setsocketopt: $!";
bind(Server,sockaddr_in(6799,INADDR_ANY)) || die "bind: $!";
listen(Server,SOMAXCONN) || die "listen: $!";

my $paddr;
my $waitedpid = 0;

sub REAPER {
    $SIG{CHLD} = \&REAPER;  # loathe sysV
    $waitedpid = wait;
}


$SIG{CHLD} = \&REAPER;


for ($waitedpid = 0; ($paddr=accept(Client,Server)) || $waitedpid; $waitedpid = 0, close Client) {
       next if $waitedpid;
       my($port,$iaddr) = sockaddr_in($paddr);
       my $name = gethostbyaddr($iaddr,AF_INET);
       print "Connection from: $name";

       spawn (sub {
          while(<CLIENT>) {
            print $_;
          }
       });
}

sub spawn {
  my $coderef=shift;

  my $pid=fork;
  if ($pid) {
     print "begat $pid";
     return; # i'm the parent
  }
  # else i'm the child -- go spawn

  open(CLIENT,  "<&Client")   || die "can't dup client to stdin";
  open(CLOUT, ">&Client")   || die "can't dup client to stdout";
 ## open(STDERR, ">&STDOUT") || die "can't dup stdout to stderr";
 exit &$coderef();
}
