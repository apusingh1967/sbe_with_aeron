java \
 -Daeron.dir=/tmp/aeron \
-Daeron.event.log.reader.include=CMD_IN_ADD_PUBLICATION,CMD_IN_ADD_SUBSCRIPTION,FRAME_OUT,FRAME_IN \
-cp aeron-all-1.44.0.jar \
io.aeron.samples.AeronStat \
| awk '
/CMD_IN_ADD_PUBLICATION/ {print "\033[33m" $0 "\033[0m"; next}   # yellow
/CMD_IN_ADD_SUBSCRIPTION/ {print "\033[33m" $0 "\033[0m"; next}  # yellow
/FRAME_OUT/ {print "\033[32m" $0 "\033[0m"; next}                # green = sent
/FRAME_IN/ {print "\033[34m" $0 "\033[0m"; next}                 # blue  = received
{print $0}
'
