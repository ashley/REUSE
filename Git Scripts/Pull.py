class Pull:
	def __init__(self,repo,apiPull):
		self.number = apiPull.number
		self.title = self.unicodePatch(apiPull.title)
		self.body = self.unicodePatch(apiPull.body)
		self.headLabel = self.unicodePatch(apiPull.head.label)
		self.comments, self.labels = self.collectIssueInfo(repo)

	def unicodePatch(self, apiObj):
		try:
			obj = str(apiObj)
		except UnicodeEncodeError:
			obj = ""
		return obj

	def collectIssueInfo(self,repo):
		comments = []
		labels = []
		issue = repo.get_issue(self.number)
		pullComments = issue.get_comments()
		for comment in pullComments:
			try:
				comments.append(str(comment.body))
			except UnicodeEncodeError:
				pass
		for label in issue.labels:
			labels.append(str(label.name))
		return comments, labels